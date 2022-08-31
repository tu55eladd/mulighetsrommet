import { useAtom } from 'jotai';
import { useErrorHandler } from 'react-error-boundary';
import { useHentBrukerdata } from '../core/api/queries/useHentBrukerdata';
import { useInnsatsgrupper } from '../core/api/queries/useInnsatsgrupper';
import { tiltaksgjennomforingsfilter } from '../core/atoms/atoms';

export function usePrepopulerFilter() {
  const [filter, setFilter] = useAtom(tiltaksgjennomforingsfilter);
  const brukerdata = useHentBrukerdata();
  const { data: innsatsgrupper } = useInnsatsgrupper();
  useErrorHandler(brukerdata?.error);

  function forcePrepopulerFilter(resetFilterTilUtgangspunkt: boolean) {
    const matchedInnsatsgruppe = innsatsgrupper?.find(gruppe => gruppe.nokkel === brukerdata?.data?.innsatsgruppe);
    if (matchedInnsatsgruppe) {
      const tiltakstyper = resetFilterTilUtgangspunkt ? [] : filter.tiltakstyper;
      const search = resetFilterTilUtgangspunkt ? '' : filter.search;
      const innsatsgruppe = resetFilterTilUtgangspunkt ? matchedInnsatsgruppe.nokkel : filter.innsatsgruppe;
      setFilter({
        search,
        tiltakstyper,
        innsatsgruppe,
      });
    }
  }

  return { forcePrepopulerFilter };
}
