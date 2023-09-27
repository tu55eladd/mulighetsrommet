import styles from "../skjema/Skjema.module.scss";
import { Button } from "@navikt/ds-react";
import { UseMutationResult } from "@tanstack/react-query";
import { Tiltaksgjennomforing, TiltaksgjennomforingRequest } from "mulighetsrommet-api-client";

interface Props {
  redigeringsModus: boolean;
  onClose: () => void;
  mutation: UseMutationResult<Tiltaksgjennomforing, unknown, TiltaksgjennomforingRequest, unknown>;
  size?: "small" | "medium";
}
export function TiltaksgjennomforingSkjemaKnapperad({
  redigeringsModus,
  onClose,
  mutation,
  size = "medium",
}: Props) {
  return (
    <div>
      <Button
        size={size}
        className={styles.button}
        onClick={onClose}
        variant="tertiary"
        type="button"
        data-testid="avbryt-knapp"
      >
        Avbryt
      </Button>
      <Button
        size={size}
        className={styles.button}
        type="submit"
        disabled={mutation.isLoading}
        data-testid="lagre-opprett-knapp"
      >
        {mutation.isLoading ? "Lagrer..." : redigeringsModus ? "Lagre gjennomføring" : "Opprett"}
      </Button>
    </div>
  );
}
