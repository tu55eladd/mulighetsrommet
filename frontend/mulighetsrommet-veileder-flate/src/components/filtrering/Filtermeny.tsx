import React from 'react';
import { Heading } from '@navikt/ds-react';
import './Filtermeny.less';
import { useAtom } from 'jotai';
import Searchfield from './Searchfield';
import { tiltaksgjennomforingsfilter } from '../../core/atoms/atoms';
import CheckboxFilter from './CheckboxFilter';
import { useInnsatsgrupper } from '../../core/api/queries/useInnsatsgrupper';
import { useTiltakstyper } from '../../core/api/queries/useTiltakstyper';
import { usePrepopulerFilter } from '../../hooks/usePrepopulerFilter';
import InnsatsgruppeFilter from './InnsatsgruppeFilter';

const Filtermeny = () => {
  const [filter, setFilter] = useAtom(tiltaksgjennomforingsfilter);

  const innsatsgrupper = useInnsatsgrupper();
  const tiltakstyper = useTiltakstyper();
  usePrepopulerFilter();

  return (
    <div className="tiltakstype-oversikt__filtermeny">
      <Heading size="medium" level="1" className="filtermeny__heading" role="heading">
        Filter
      </Heading>
      <Searchfield sokefilter={filter.search!} setSokefilter={(search: string) => setFilter({ ...filter, search })} />
      <InnsatsgruppeFilter
        accordionNavn="Innsatsgruppe"
        option={filter.innsatsgruppe!}
        key={filter.innsatsgruppe}
        setOption={innsatsgruppe => setFilter({ ...filter, innsatsgruppe })}
        data={
          innsatsgrupper.data?.map(innsatsgruppe => {
            return innsatsgruppe.tittel;
          }) ?? []
        }
        isLoading={innsatsgrupper.isLoading}
        isError={innsatsgrupper.isError}
        defaultOpen
      />
      <CheckboxFilter
        accordionNavn="Tiltakstyper"
        options={filter.tiltakstyper!}
        setOptions={tiltakstyper => setFilter({ ...filter, tiltakstyper })}
        data={
          tiltakstyper.data?.map(tiltakstype => {
            return {
              id: tiltakstype._id,
              tittel: tiltakstype.tiltakstypeNavn,
            };
          }) ?? []
        }
        isLoading={tiltakstyper.isLoading}
        isError={tiltakstyper.isError}
        sortert
        defaultOpen={filter.tiltakstyper.length > 0}
      />
    </div>
  );
};

export default Filtermeny;
