import { Alert, Heading, Link, Loader } from "@navikt/ds-react";
import { useNavigate } from "react-router-dom";
import { useTiltaksgjennomforingById } from "../api/tiltaksgjennomforing/useTiltaksgjennomforingById";
import { formaterDato } from "../utils/Utils";
import styles from "./TiltaksgjennomforingPage.module.scss";

interface TiltaksgjennomforingPageProps {
  fagansvarlig?: boolean;
}

export function TiltaksgjennomforingPage({
  fagansvarlig = false,
}: TiltaksgjennomforingPageProps) {
  const optionalTiltaksgjennomforing = useTiltaksgjennomforingById();
  const navigate = useNavigate();

  const navigerTilbake = () => navigate(-1);

  if (optionalTiltaksgjennomforing.error) {
    return (
      <Alert variant="warning">
        <div>Noe gikk galt ved henting av data om tiltaksgjennomføring</div>
        <Link href="/">Til forside</Link>
      </Alert>
    );
  }

  if (optionalTiltaksgjennomforing.isFetching) {
    return (
      <div
        style={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          flexDirection: "column",
        }}
      >
        <Loader />
        <p>Laster data om tiltaksgjennomføring</p>
      </div>
    );
  }

  if (!optionalTiltaksgjennomforing.data) {
    return (
      <Alert variant="warning">Klarte ikke finne tiltaksgjennomføring</Alert>
    );
  }

  const tiltaksgjennomforing = optionalTiltaksgjennomforing.data;
  return (
    <div className={styles.container}>
      <Link href="#" onClick={navigerTilbake}>
        {fagansvarlig ? "Tilbake til tiltakstype" : "Tilbake"}
      </Link>

      <Heading size="large" level="1">
        {tiltaksgjennomforing.tiltaksnummer} - {tiltaksgjennomforing.navn}
      </Heading>
      <p>
        Tiltaksgjennomføringen har startdato:{" "}
        {formaterDato(tiltaksgjennomforing.fraDato)} og sluttdato{" "}
        {formaterDato(tiltaksgjennomforing.tilDato)}
      </p>
      <dl>
        <dt>Tiltaksnummer</dt>
        <dd>{tiltaksgjennomforing.tiltaksnummer}</dd>
        <dt>Tiltakstype</dt>
        <dd>{tiltaksgjennomforing.tiltakstypeNavn}</dd>
        <dt>Kode for tiltakstype:</dt>
        <dd>{tiltaksgjennomforing.tiltakskode}</dd>
        <dt>Virksomhetsnummer</dt>
        <dd>{tiltaksgjennomforing.virksomhetsnummer}</dd>
        <dt>Startdato</dt>
        <dd>{formaterDato(tiltaksgjennomforing.fraDato)} </dd>
        <dt>Sluttdato</dt>
        <dd>{formaterDato(tiltaksgjennomforing.tilDato)} </dd>
      </dl>

      {/**
       * TODO Implementere skjema for opprettelse av tiltaksgjennomføring
       */}
      {/* <p>Her kan du opprette en gjennomføring</p>
      <Formik<Values>
        initialValues={{
          tiltakgjennomforingId: "",
          sakId: "",
        }}
        validationSchema={toFormikValidationSchema(Schema)}
        onSubmit={(values, actions) => {
          setTimeout(() => {
            alert(JSON.stringify(values, null, 2));
            actions.setSubmitting(false);
          }, 1000);
        }}
      >
        {() => (
          <Form>
            <Tekstfelt
              name="tiltakgjennomforingId"
              type="text"
              label="ID for tiltaksgjennomføring"
            />
            <Tekstfelt name="sakId" type="text" label="ID for sak" />
            <button type="submit">Opprett</button>
          </Form>
        )}
      </Formik> */}
    </div>
  );
}
