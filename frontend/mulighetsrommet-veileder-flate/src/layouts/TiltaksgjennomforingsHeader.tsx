import { VeilederflateTiltak } from "@mr/api-client";
import { TiltaksgjennomforingStatusTag } from "@mr/frontend-common";
import { gjennomforingIsAktiv } from "@mr/frontend-common/utils/utils";
import { BodyLong, Heading, HStack, VStack } from "@navikt/ds-react";
import { erKurstiltak } from "../utils/Utils";
import styles from "./TiltaksgjennomforingsHeader.module.scss";

interface Props {
  tiltaksgjennomforing: VeilederflateTiltak;
}

const TiltaksgjennomforingsHeader = ({ tiltaksgjennomforing }: Props) => {
  const { navn, beskrivelse, tiltakstype } = tiltaksgjennomforing;
  return (
    <>
      <HStack align="center" gap="2" className={styles.tiltaksgjennomforing_title}>
        <Heading level="1" size="xlarge">
          <VStack>
            {erKurstiltak(tiltakstype.tiltakskode, tiltakstype.arenakode) ? (
              <>
                {navn}
                <BodyLong size="medium" textColor="subtle">
                  {tiltakstype.navn}
                </BodyLong>
              </>
            ) : (
              <>
                {tiltakstype.navn}
                <BodyLong size="medium" textColor="subtle">
                  {navn}
                </BodyLong>
              </>
            )}
          </VStack>
        </Heading>
        {!gjennomforingIsAktiv(tiltaksgjennomforing.status.status) && (
          <TiltaksgjennomforingStatusTag status={tiltaksgjennomforing.status} />
        )}
      </HStack>
      {tiltakstype.beskrivelse && (
        <BodyLong size="large" className={styles.beskrivelse} style={{ whiteSpace: "pre-wrap" }}>
          {tiltakstype.beskrivelse}
        </BodyLong>
      )}
      {beskrivelse && (
        <BodyLong style={{ whiteSpace: "pre-wrap" }} textColor="subtle" size="medium">
          {beskrivelse}
        </BodyLong>
      )}
    </>
  );
};

export default TiltaksgjennomforingsHeader;
