import { useQueryClient } from "@tanstack/react-query";
import { Tiltakstypestatus } from "mulighetsrommet-api-client";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useHentAnsatt } from "../../api/ansatt/useHentAnsatt";
import { useAvtale } from "../../api/avtaler/useAvtale";
import { useNavEnheter } from "../../api/enhet/useNavEnheter";
import { useTiltakstyper } from "../../api/tiltakstyper/useTiltakstyper";
import { useUtkast } from "../../api/utkast/useUtkast";
import { ContainerLayout } from "../../layouts/ContainerLayout";
import { inneholderUrl } from "../../utils/Utils";
import { Header } from "../../components/detaljside/Header";
import { Laster } from "../../components/laster/Laster";
import styles from "../../components/skjema/Skjema.module.scss";
import { AvtaleSchema } from "../../components/avtaler/AvtaleSchema";
import { AvtaleUtkastData } from "../../components/avtaler/AvtaleSkjemaConst";
import { AvtaleSkjemaContainer } from "../../components/avtaler/AvtaleSkjemaContainer";
import { AvtalestatusTag } from "../../components/statuselementer/AvtalestatusTag";
import { Heading } from "@navikt/ds-react";

const AvtaleSkjemaPage = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [searchParams] = useSearchParams();
  const { data: avtale, isLoading: avtaleLoading } = useAvtale();
  const { data: utkast, isLoading: utkastLoading } = useUtkast(
    AvtaleSchema,
    searchParams.get("utkastId") || undefined,
  );
  const { data: tiltakstyper, isLoading: isLoadingTiltakstyper } = useTiltakstyper(
    { status: Tiltakstypestatus.AKTIV },
    1,
  );
  const { data: ansatt, isLoading: isLoadingAnsatt } = useHentAnsatt();
  const { data: enheter, isLoading: isLoadingEnheter } = useNavEnheter();

  const utkastModus = utkast && inneholderUrl(utkast?.id);
  const redigeringsModus = utkastModus || (avtale && inneholderUrl(avtale?.id));

  const navigerTilbake = () => {
    navigate(-1);
  };

  if (utkastLoading || avtaleLoading) {
    return <Laster size="xlarge" tekst={utkastLoading ? "Laster utkast..." : "Laster avtale..."} />;
  }

  function refetchUtkast() {
    queryClient.refetchQueries({
      queryKey: ["utkast"],
    });
  }

  return (
    <main>
      <Header>
        <Heading size="large" level="2">
          {redigeringsModus
            ? utkastModus
              ? "Rediger utkast"
              : "Rediger avtale"
            : "Opprett ny avtale"}
        </Heading>
        {avtale ? <AvtalestatusTag avtale={avtale} /> : null}
      </Header>

      <ContainerLayout>
        <div className={styles.skjema}>
          {isLoadingAnsatt || isLoadingTiltakstyper || isLoadingEnheter ? <Laster /> : null}
          <div className={styles.skjema_content}>
            {!tiltakstyper?.data || !ansatt || !enheter ? null : (
              <AvtaleSkjemaContainer
                onClose={() => {
                  refetchUtkast();
                  navigerTilbake();
                }}
                onSuccess={(id) => navigate(`/avtaler/${id}`)}
                tiltakstyper={tiltakstyper.data}
                ansatt={ansatt}
                enheter={enheter}
                avtale={avtale}
                avtaleUtkast={utkast?.utkastData as AvtaleUtkastData}
                redigeringsModus={redigeringsModus!}
              />
            )}
          </div>
        </div>
      </ContainerLayout>
    </main>
  );
};

export default AvtaleSkjemaPage;