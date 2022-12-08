import { FiKey } from "react-icons/fi";
import { defineType, defineField } from "sanity";

export const nokkelinfo = defineType({
  name: "nokkelinfo",
  title: "Nøkkelinfo",
  type: "object",
  icon: FiKey,
  fields: [
    defineField({
      name: "tittel",
      title: "Nøkkelinfo tittel",
      type: "string",
      validation: (Rule) =>
        Rule.custom((field, context) =>
          context.document.tittel && field === undefined
            ? "Dette feltet kan ikke være tomt."
            : true
        ),
    }),
    defineField({
      name: "innhold",
      title: "Innhold nøkkelinfo",
      type: "string",
      validation: (Rule) =>
        Rule.custom((field, context) =>
          context.document.innhold && field === undefined
            ? "Dette feltet kan ikke være tomt."
            : true
        ),
    }),
    defineField({
      name: "hjelpetekst",
      title: "Hjelpetekst til nøkkelinfo",
      type: "string",
    }),
  ],
  preview: {
    select: {
      title: "tittel",
    },
  },
});
