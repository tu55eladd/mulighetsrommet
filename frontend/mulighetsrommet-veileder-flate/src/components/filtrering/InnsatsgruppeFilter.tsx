import { Accordion, Alert, Loader, Radio, RadioGroup } from '@navikt/ds-react';
import { useAtom } from 'jotai';
import { InnsatsgruppeNokler } from '../../core/api/models';
import { useInnsatsgrupper } from '../../core/api/queries/useInnsatsgrupper';
import { tiltaksgjennomforingsfilter } from '../../core/atoms/atoms';
import { kebabCase } from '../../utils/Utils';
import styles from './Filtermeny.module.scss';

interface InnsatsgruppeFilterProps<T extends { id: string; tittel: string; nokkel?: InnsatsgruppeNokler }> {
  accordionNavn: string;
  option?: InnsatsgruppeNokler;
  setOption: (type: InnsatsgruppeNokler) => void;
  data: T[];
  isLoading: boolean;
  isError: boolean;
  defaultOpen?: boolean;
}

const InnsatsgruppeAccordion = <T extends { id: string; tittel: string; nokkel?: InnsatsgruppeNokler }>({
  accordionNavn,
  option,
  setOption,
  data,
  isLoading,
  isError,
  defaultOpen = false,
}: InnsatsgruppeFilterProps<T>) => {
  const radiobox = (option: T) => {
    return (
      <Radio
        value={option.nokkel}
        key={`${option.id}`}
        data-testid={`filter_checkbox_${kebabCase(option?.tittel ?? '')}`}
      >
        {option.tittel}
      </Radio>
    );
  };

  return (
    <Accordion role="menu" className={styles.accordion}>
      <Accordion.Item defaultOpen={defaultOpen}>
        <Accordion.Header data-testid={`filter_accordionheader_${kebabCase(accordionNavn)}`}>
          {accordionNavn}
        </Accordion.Header>
        <Accordion.Content role="menuitem" data-testid={`filter_accordioncontent_${kebabCase(accordionNavn)}`}>
          {isLoading && <Loader size="xlarge" />}
          {data && (
            <RadioGroup
              legend=""
              hideLegend
              size="small"
              onChange={(e: InnsatsgruppeNokler) => {
                setOption(e);
              }}
              value={option}
            >
              {data.map(radiobox)}
            </RadioGroup>
          )}
          {isError && <Alert variant="error">Det har skjedd en feil</Alert>}
        </Accordion.Content>
      </Accordion.Item>
    </Accordion>
  );
};

function InnsatsgruppeFilter() {
  const [filter, setFilter] = useAtom(tiltaksgjennomforingsfilter);
  const innsatsgrupper = useInnsatsgrupper();
  return (
    <InnsatsgruppeAccordion
      accordionNavn="Innsatsgruppe"
      option={filter.innsatsgruppe?.nokkel}
      key={filter.innsatsgruppe?.nokkel}
      setOption={innsatsgruppe => {
        const foundInnsatsgruppe = innsatsgrupper.data?.find(gruppe => gruppe.nokkel === innsatsgruppe);
        if (foundInnsatsgruppe) {
          setFilter({
            ...filter,
            innsatsgruppe: {
              id: foundInnsatsgruppe?._id,
              tittel: foundInnsatsgruppe?.tittel,
              nokkel: foundInnsatsgruppe?.nokkel,
            },
          });
        }
      }}
      data={
        innsatsgrupper.data?.map(innsatsgruppe => {
          return { id: innsatsgruppe._id, tittel: innsatsgruppe.tittel, nokkel: innsatsgruppe.nokkel };
        }) ?? []
      }
      isLoading={innsatsgrupper.isLoading}
      isError={innsatsgrupper.isError}
      defaultOpen
    />
  );
}

export default InnsatsgruppeFilter;
