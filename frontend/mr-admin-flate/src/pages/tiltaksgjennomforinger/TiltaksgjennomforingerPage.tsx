import { useTitle } from "mulighetsrommet-frontend-common";
import { ErrorBoundary } from "react-error-boundary";
import { Tiltaksgjennomforingfilter } from "../../components/filter/Tiltaksgjennomforingfilter";
import { TiltaksgjennomforingsTabell } from "../../components/tabell/TiltaksgjennomforingsTabell";
import { ContainerLayout } from "../../layouts/ContainerLayout";
import { HeaderBanner } from "../../layouts/HeaderBanner";
import { MainContainer } from "../../layouts/MainContainer";
import { ErrorFallback } from "../../main";
import { useAdminTiltaksgjennomforinger } from "../../api/tiltaksgjennomforing/useAdminTiltaksgjennomforinger";
import { tiltaksgjennomforingfilterAtom } from "../../api/atoms";

export function TiltaksgjennomforingerPage() {
  useTitle("Tiltaksgjennomføringer");
  const { data: tiltaksgjennomforinger, isLoading: tiltaksgjennomforingerIsLoading } =
    useAdminTiltaksgjennomforinger(tiltaksgjennomforingfilterAtom);
  return (
    <>
      <HeaderBanner heading="Oversikt over tiltaksgjennomføringer" />
      <MainContainer>
        <ContainerLayout>
          <Tiltaksgjennomforingfilter filterAtom={tiltaksgjennomforingfilterAtom} />
          <ErrorBoundary FallbackComponent={ErrorFallback}>
            <TiltaksgjennomforingsTabell
              isLoading={tiltaksgjennomforingerIsLoading}
              paginerteTiltaksgjennomforinger={tiltaksgjennomforinger}
            />
          </ErrorBoundary>
        </ContainerLayout>
      </MainContainer>
    </>
  );
}
