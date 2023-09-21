import { BaseText, BaseElement, Element, Text, Node, Descendant } from "slate";
import type {
  PortableTextBlock,
  PortableTextSpan,
  PortableTextMarkDefinition,
} from '@portabletext/types'

// BaseElement doesn't expose the type property for some reason
// https://github.com/ianstormtaylor/slate/issues/4915
declare module 'slate' {
  export interface BaseElement {
    type: string;
    markDefs?: PortableTextMarkDefinition[];
    url?: string;
  }
  export interface BaseText {
    italic?: boolean;
    bold?: boolean;
  }
}

export const slateToPortableText = (nodes: Descendant[]): PortableTextBlock[] => {
  return nodes
    .map((node: Element | Text) => {
      if (!Element.isElement(node)) throw Error("Unsupported slate node");

      // Only supported blocks as of now, is normal, bulleted, and heading
      if (node.type === "bulleted-list") {
        return bulletedListBlock(node);
      }
      if (node.type === "heading-one") {
        return {
          ...toPortableTextBlock(node),
          style: "h1",
        };
      }
      if (node.type && node.type !== "paragraph") {
        throw Error(`Unsupported block type: ${node.type}`)
      }
      return toPortableTextBlock(node);
    })
    .filter(Boolean) as PortableTextBlock[];
}

const toPortableTextSpan = (span: BaseText): PortableTextSpan => {
  return {
    _type: "span",
    text: Node.string(span),
    marks: findMark(span)
  }
}

const linkToPortableTextSpans = (link: BaseElement): [PortableTextSpan[], PortableTextMarkDefinition] => {
  if (!link.url) throw Error("link does not have url");

  const markDef: PortableTextMarkDefinition = {
      _type: "link",
      _key: link.url,
      href: link.url,
  };

  const spans = link.children
    .map(s => toPortableTextSpan(s as BaseText))
    .map(s => ({
      ...s,
      marks: [...(s?.marks || []), link.url]
    })) as PortableTextSpan[];

  return [spans, markDef];
}

const toPortableTextBlock = (node: Descendant): PortableTextBlock => {
  if (!Element.isElement(node)) throw Error(`Unsupported slate node: ${node}`);

  const children = []
  const markDefs = [];

  for (const child of node.children) {
    if (Element.isElementType(child, 'link')) {
      const [spans, markDef] = linkToPortableTextSpans(child);
      markDefs.push(markDef);
      children.push(...spans);
    } else {
      children.push(toPortableTextSpan(child));
    }
  }

  return ({
    _type: "block",
    markDefs,
    children,
  })
}

const bulletedListBlock = (node: BaseElement): PortableTextBlock => {
  return ({
    _type: "block",
    markDefs: node.markDefs,
    children: node.children.map(child => ({
      ...toPortableTextBlock(child),
      listItem: "bullet",
    })),
  });
}

function findMark(span: BaseText) {
  const marks = [];
  if (span.bold) {
    marks.push("strong");
  }
  if (span.italic) {
    marks.push("em");
  }
  return marks.length ? marks : undefined;
}