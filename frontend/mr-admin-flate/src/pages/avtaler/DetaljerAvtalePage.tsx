import { Alert, Tabs } from "@navikt/ds-react";
import { useAvtale } from "../../api/avtaler/useAvtale";
import { useFeatureToggles } from "../../api/features/feature-toggles";
import { Header } from "../../components/detaljside/Header";
import { Avtalestatus } from "../../components/statuselementer/Avtalestatus";
import { ContainerLayout } from "../../layouts/ContainerLayout";
import { MainContainer } from "../../layouts/MainContainer";
import { Avtaleinfo } from "./Avtaleinfo";
import { NokkeltallForAvtale } from "./nokkeltall/NokkeltallForAvtale";
import styles from "./DetaljerAvtalePage.module.scss";
import { Laster } from "../../components/laster/Laster";
import { Link, useParams } from "react-router-dom";
import { TiltaksgjennomforingerForAvtale } from "./tiltaksgjennomforinger/TiltaksgjennomforingerForAvtale";
import { useAtom } from "jotai";
import { AvtaleTabs, avtaleFilter } from "../../api/atoms";

export function DetaljerAvtalePage() {
  const { avtaleId } = useParams<{ avtaleId: string }>();
  if (!avtaleId) {
    throw new Error("Fant ingen avtaleId i url");
  }
  const { data: avtale, isLoading } = useAvtale(avtaleId);
  const [filter, setFilter] = useAtom(avtaleFilter);
  const { data } = useFeatureToggles();

  if (!avtale && isLoading) {
    return (
      <main>
        <Laster tekst="Laster avtale" />
      </main>
    );
  }

  if (!avtale) {
    return (
      <Alert variant="warning">
        Klarte ikke finne avtale
        <div>
          <Link to="/">Til forside</Link>
        </div>
      </Alert>
    );
  }

  return (
    <MainContainer>
      <Header>
        <div className={styles.header}>
          <span>{avtale?.navn ?? "..."}</span>
          <Avtalestatus avtale={avtale} />
        </div>{" "}
      </Header>
      <Tabs
        value={filter.avtaleTab}
        onChange={(tab) =>
          setFilter({ ...filter, avtaleTab: tab as AvtaleTabs })
        }
      >
        <Tabs.List className={styles.list}>
          <Tabs.Tab value="avtaleinfo" label="Avtaleinfo" />
          <Tabs.Tab value="tiltaksgjennomforinger" label="Gjennomføringer" />
          {data?.["mulighetsrommet.admin-flate-vis-nokkeltall"] ? (
            <Tabs.Tab value="nokkeltall" label="Nøkkeltall" />
          ) : null}
        </Tabs.List>
        <Tabs.Panel value="avtaleinfo" className="h-24 w-full bg-gray-50 p-4">
          <ContainerLayout>
            <Avtaleinfo />
          </ContainerLayout>
        </Tabs.Panel>
        <Tabs.Panel
          value="tiltaksgjennomforinger"
          className="h-24 w-full bg-gray-50 p-4"
        >
          <ContainerLayout>
            <TiltaksgjennomforingerForAvtale />
          </ContainerLayout>
        </Tabs.Panel>
        <Tabs.Panel value="nokkeltall" className="h-24 w-full bg-gray-50 p-4">
          <ContainerLayout>
            <NokkeltallForAvtale />
          </ContainerLayout>
        </Tabs.Panel>
      </Tabs>
    </MainContainer>
  );
}
