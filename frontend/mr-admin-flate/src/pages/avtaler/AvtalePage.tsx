import { Alert, Heading, Tabs } from "@navikt/ds-react";
import { Toggles } from "mulighetsrommet-api-client";
import { useTitle } from "mulighetsrommet-frontend-common";
import { Link, Outlet, useLocation, useMatch } from "react-router-dom";
import { useAvtale } from "../../api/avtaler/useAvtale";
import { useFeatureToggle } from "../../api/features/feature-toggles";
import { DupliserAvtale } from "../../components/avtaler/DupliserAvtale";
import { Header } from "../../components/detaljside/Header";
import headerStyles from "../../components/detaljside/Header.module.scss";
import { Laster } from "../../components/laster/Laster";
import { Brodsmuler } from "../../components/navigering/Brodsmuler";
import { AvtalestatusTag } from "../../components/statuselementer/AvtalestatusTag";
import { useNavigateAndReplaceUrl } from "../../hooks/useNavigateWithoutReplacingUrl";
import { ContainerLayout } from "../../layouts/ContainerLayout";
import commonStyles from "../Page.module.scss";
import styles from "./DetaljerAvtalePage.module.scss";

export function AvtalePage() {
  const { pathname } = useLocation();
  const { navigateAndReplaceUrl } = useNavigateAndReplaceUrl();
  const { data: showNotater } = useFeatureToggle(Toggles.MULIGHETSROMMET_ADMIN_FLATE_SHOW_NOTATER);
  const { data: avtale, isPending } = useAvtale();
  useTitle(`Avtale ${avtale?.navn ? `- ${avtale.navn}` : ""}`);
  const erPaaGjennomforingerForAvtale = useMatch("/avtaler/:avtaleId/tiltaksgjennomforinger");

  if (isPending) {
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

  const currentTab = () => {
    if (pathname.includes("notater")) {
      return "notater";
    } else if (pathname.includes("tiltaksgjennomforinger")) {
      return "tiltaksgjennomforinger";
    } else {
      return "info";
    }
  };

  return (
    <main className={styles.avtaleinfo}>
      <Brodsmuler
        brodsmuler={[
          { tittel: "Forside", lenke: "/" },
          { tittel: "Avtaler", lenke: "/avtaler" },
          { tittel: "Avtaledetaljer", lenke: `/avtaler/${avtale.id}` },
          erPaaGjennomforingerForAvtale
            ? {
                tittel: "Avtalens gjennomføringer",
                lenke: `/avtaler/${avtale.id}/tiltaksgjennomforinger`,
              }
            : undefined,
        ]}
      />
      <Header>
        <div className={headerStyles.tiltaksnavn_status}>
          <Heading size="large" level="2">
            {avtale.navn ?? "..."}
          </Heading>
          <AvtalestatusTag avtale={avtale} />
          <DupliserAvtale avtale={avtale} />
        </div>
      </Header>
      <Tabs value={currentTab()}>
        <Tabs.List className={commonStyles.list}>
          <Tabs.Tab
            value="info"
            label="Avtaleinfo"
            onClick={() => navigateAndReplaceUrl(`/avtaler/${avtale.id}`)}
            aria-controls="panel"
          />
          {showNotater && (
            <Tabs.Tab
              value="notater"
              label="Notater"
              onClick={() => navigateAndReplaceUrl(`/avtaler/${avtale.id}/notater`)}
              aria-controls="panel"
              data-testid="notater-tab"
            />
          )}
          <Tabs.Tab
            value="tiltaksgjennomforinger"
            label="Gjennomføringer"
            onClick={() => navigateAndReplaceUrl(`/avtaler/${avtale.id}/tiltaksgjennomforinger`)}
            aria-controls="panel"
            data-testid="gjennomforinger-tab"
          />
        </Tabs.List>
        <ContainerLayout>
          <div id="panel">
            <Outlet />
          </div>
        </ContainerLayout>
      </Tabs>
    </main>
  );
}
