import { useAtom } from 'jotai';
import { useEffect } from 'react';
import { useHentBrukerdata } from '../core/api/queries/useHentBrukerdata';
import { useInnsatsgrupper } from '../core/api/queries/useInnsatsgrupper';
import { tiltaksgjennomforingsfilter } from '../core/atoms/atoms';

export function usePrepopulerFilter() {
  const [filter, setFilter] = useAtom(tiltaksgjennomforingsfilter);
  const brukerdata = useHentBrukerdata();
  const { data: innsatsgrupper } = useInnsatsgrupper();
  const data = brukerdata?.data;

  useEffect(() => {
    if (data && innsatsgrupper) {
      forcePrepopulerFilter(false);
    }
  }, [data, innsatsgrupper]);

  function forcePrepopulerFilter(resetFilterTilUtgangspunkt: boolean) {
    const matchedInnsatsgruppe = innsatsgrupper?.find(gruppe => gruppe.nokkel === brukerdata?.data?.innsatsgruppe);
    if (matchedInnsatsgruppe) {
      const tiltakstyper = resetFilterTilUtgangspunkt ? [] : filter.tiltakstyper;
      const search = resetFilterTilUtgangspunkt ? '' : filter.search;
      const innsatsgrupper = resetFilterTilUtgangspunkt
        ? [{ id: matchedInnsatsgruppe._id, ...matchedInnsatsgruppe }]
        : [...filter.innsatsgrupper];
      setFilter({
        search,
        tiltakstyper,
        innsatsgrupper,
      });
    }
  }

  return { forcePrepopulerFilter };
}
