import { Button, Heading, HStack, Modal } from "@navikt/ds-react";
import { Avtale, Tiltaksgjennomforing } from "mulighetsrommet-api-client";
import { RedaksjoneltInnholdForm } from "../redaksjonelt-innhold/RedaksjoneltInnholdForm";
import { useFormContext } from "react-hook-form";
import { InferredTiltaksgjennomforingSchema } from "./TiltaksgjennomforingSchema";
import { useState } from "react";
import { RedaksjoneltInnholdContainer } from "../redaksjonelt-innhold/RedaksjoneltInnholdContainer";
import styles from "../modal/LeggTilGjennomforingModal.module.scss";
import { TiltaksgjennomforingerListe } from "./TiltaksgjennomforingerListe";

interface Props {
  avtale: Avtale;
}

export function TiltakgjennomforingRedaksjoneltInnholdForm({ avtale }: Props) {
  const [key, setKey] = useState(0);
  const [modalOpen, setModalOpen] = useState<boolean>(false);

  const { setValue } = useFormContext<InferredTiltaksgjennomforingSchema>();

  function kopierRedaksjoneltInnhold({ beskrivelse, faneinnhold }: Tiltaksgjennomforing | Avtale) {
    setValue("beskrivelse", beskrivelse ?? null);
    setValue("faneinnhold", faneinnhold ?? null);
  }

  return (
    <>
      <RedaksjoneltInnholdContainer>
        <HStack justify="end">
          <Button
            size="small"
            variant="tertiary"
            type="button"
            title="Gjenopprett til redaksjonelt innhold fra avtale"
            onClick={() => {
              kopierRedaksjoneltInnhold(avtale);

              // Ved å endre `key` så tvinger vi en update av den underliggende Slate-komponenten slik at
              // innhold i komponenten blir resatt til å reflektere den nye tilstanden i skjemaet
              setKey(key + 1);
            }}
          >
            Gjenopprett til redaksjonelt innhold fra avtale
          </Button>
          <Button
            size="small"
            variant="tertiary"
            type="button"
            title="Kopier redaksjonelt innhold fra en annen gjennomføring under den samme avtalen"
            onClick={() => setModalOpen(true)}
          >
            Kopier redaksjonelt innhold fra gjennomføring
          </Button>
        </HStack>
      </RedaksjoneltInnholdContainer>

      <RedaksjoneltInnholdForm
        key={`redaksjonelt-innhold-${key}`}
        tiltakstype={avtale.tiltakstype}
      />

      <Modal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        className={styles.modal_container}
        aria-label="modal"
        width="50rem"
      >
        <Modal.Header closeButton>
          <Heading size="medium">Kopier redaksjonelt innhold fra gjennomføring</Heading>
        </Modal.Header>
        <Modal.Body className={styles.modal_content}>
          <TiltaksgjennomforingerListe
            filter={{ avtale: avtale.id }}
            action={(gjennomforing) => (
              <Button
                size="small"
                variant="tertiary"
                type="button"
                onClick={() => {
                  kopierRedaksjoneltInnhold(gjennomforing);
                  setModalOpen(false);
                }}
              >
                Kopier innhold
              </Button>
            )}
          />
        </Modal.Body>
      </Modal>
    </>
  );
}
