import { Alert, BodyLong, Heading } from "@navikt/ds-react";
import styles from "./Detaljerfane.module.scss";
import FaneTiltaksinformasjon from "./FaneTiltaksinformasjon";
import { RedaksjoneltInnhold } from "../RedaksjoneltInnhold";
import { ArbitraryTypedObject } from "@portabletext/types";

interface DetaljerFaneProps {
  tiltaksgjennomforingAlert?: string | null;
  tiltakstypeAlert?: string | null;
  tiltaksgjennomforing?: ArbitraryTypedObject | ArbitraryTypedObject[] | null;
  tiltakstype?: ArbitraryTypedObject | ArbitraryTypedObject[] | null;
}

const DetaljerFane = ({
  tiltaksgjennomforingAlert,
  tiltakstypeAlert,
  tiltaksgjennomforing,
  tiltakstype,
}: DetaljerFaneProps) => {
  const harInnhold = !!(
    tiltaksgjennomforingAlert ||
    tiltakstypeAlert ||
    tiltaksgjennomforing ||
    tiltakstype
  );
  return (
    <FaneTiltaksinformasjon className={styles.faneinnhold_container} harInnhold={harInnhold}>
      <Heading level="2" size="small">
        Generell informasjon
      </Heading>
      {tiltakstypeAlert && (
        <Alert variant="info" className={styles.preWrap}>
          {tiltakstypeAlert}
        </Alert>
      )}
      {tiltakstype && (
        <BodyLong as="div" size="small">
          <RedaksjoneltInnhold value={tiltakstype} />
        </BodyLong>
      )}
      {(tiltaksgjennomforing || tiltaksgjennomforingAlert) && (
        <div className={styles.lokal_informasjon}>
          <Heading level="2" size="small">
            Lokal informasjon
          </Heading>
          {tiltaksgjennomforingAlert && (
            <Alert variant="info" className={styles.preWrap}>
              {tiltaksgjennomforingAlert}
            </Alert>
          )}
          {tiltaksgjennomforing && (
            <BodyLong as="div" textColor="subtle" size="small">
              <RedaksjoneltInnhold value={tiltaksgjennomforing} />
            </BodyLong>
          )}
        </div>
      )}
    </FaneTiltaksinformasjon>
  );
};

export default DetaljerFane;
