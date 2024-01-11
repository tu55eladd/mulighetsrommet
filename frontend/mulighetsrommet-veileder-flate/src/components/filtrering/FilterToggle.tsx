import { Accordion, ToggleGroup } from "@navikt/ds-react";
import { ApentForInnsok, HarDeltMedBruker } from "mulighetsrommet-api-client";
import "./ApentForInnsokToggle.module.scss";
import { useAtom } from "jotai";
import { addOrRemove } from "../../utils/Utils";
import React from "react";
import { filterAccordionAtom } from "../../core/atoms/atoms";

export interface Props {
  accordionHeader: string;
  value: string;
  onChange(value: any): void;
  venstreTekst: React.ReactNode;
  midtTekst?: React.ReactNode;
  hoyreTekst: React.ReactNode;
  venstreValue: string;
  midtValue: string;
  hoyreValue: string;
  accordionIsOpenValue: string;
}

export function FilterToggle({
  accordionHeader,
  value,
  onChange,
  venstreTekst,
  midtTekst = "Begge",
  hoyreTekst,
  venstreValue,
  midtValue,
  hoyreValue,
  accordionIsOpenValue,
}: Props) {
  const [accordionsOpen, setAccordionsOpen] = useAtom(filterAccordionAtom);
  function onToggleChanged(value: string) {
    if (Object.values(ApentForInnsok).includes(value as ApentForInnsok)) {
      onChange(value as ApentForInnsok);
    } else if (Object.values(HarDeltMedBruker).includes(value as HarDeltMedBruker)) {
      onChange(value as HarDeltMedBruker);
    }
  }

  return (
    <Accordion.Item open={accordionsOpen.includes(accordionIsOpenValue)}>
      <Accordion.Header
        onClick={() => {
          setAccordionsOpen([...addOrRemove(accordionsOpen, accordionIsOpenValue)]);
        }}
      >
        {accordionHeader}
      </Accordion.Header>
      <Accordion.Content>
        <ToggleGroup size="small" defaultValue={value} onChange={onToggleChanged}>
          <ToggleGroup.Item value={venstreValue}>{venstreTekst}</ToggleGroup.Item>
          <ToggleGroup.Item value={midtValue}>{midtTekst}</ToggleGroup.Item>
          <ToggleGroup.Item value={hoyreValue}>{hoyreTekst}</ToggleGroup.Item>
        </ToggleGroup>
      </Accordion.Content>
    </Accordion.Item>
  );
}
