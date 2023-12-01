import { BodyShort, Button, Checkbox, Heading, Modal } from "@navikt/ds-react";
import {
  Bruker,
  DelMedBruker,
  VeilederflateTiltaksgjennomforing,
} from "mulighetsrommet-api-client";
import { PORTEN } from "mulighetsrommet-frontend-common/constants";
import { useReducer } from "react";
import { mulighetsrommetClient } from "../../../core/api/clients";
import { logEvent } from "../../../core/api/logger";
import { useHentDeltMedBrukerStatus } from "../../../core/api/queries/useHentDeltMedbrukerStatus";
import { byttTilDialogFlate } from "../../../utils/DialogFlateUtils";
import { erPreview } from "../../../utils/Utils";
import modalStyles from "../Modal.module.scss";
import { StatusModal } from "../StatusModal";
import { DelMedBrukerContent, MAKS_ANTALL_TEGN_DEL_MED_BRUKER } from "./DelMedBrukerContent";
import delemodalStyles from "./Delemodal.module.scss";
import { Actions, State } from "./DelemodalActions";

export const logDelMedbrukerEvent = (
  action:
    | "Åpnet dialog"
    | "Delte med bruker"
    | "Del med bruker feilet"
    | "Avbrutt del med bruker"
    | "Sett hilsen"
    | "Sett intro",
) => {
  logEvent("mulighetsrommet.del-med-bruker", { value: action });
};

interface DelemodalProps {
  modalOpen: boolean;
  lukkModal: () => void;
  brukernavn?: string;
  chattekst: string;
  veiledernavn?: string;
  brukerFnr: string;
  tiltaksgjennomforing: VeilederflateTiltaksgjennomforing;
  brukerdata: Bruker;
  harDeltMedBruker?: DelMedBruker;
}

export function reducer(state: State, action: Actions): State {
  switch (action.type) {
    case "Avbryt":
      return {
        ...state,
        sendtStatus: "IKKE_SENDT",
        hilsen: action.payload.tekster.originalHilsen,
        skrivPersonligMelding: false,
        skrivPersonligIntro: false,
        deletekst: action.payload.tekster.deletekst,
        introtekst: action.payload.tekster.introtekst,
        venterPaaSvarFraBruker: false,
      };
    case "Send melding":
      return { ...state, sendtStatus: "SENDER" };
    case "Sendt ok":
      return { ...state, sendtStatus: "SENDT_OK", dialogId: action.payload };
    case "Sending feilet":
      return { ...state, sendtStatus: "SENDING_FEILET" };
    case "Sett hilsen":
      return { ...state, hilsen: action.payload, sendtStatus: "IKKE_SENDT" };
    case "Sett intro":
      return { ...state, introtekst: action.payload, sendtStatus: "IKKE_SENDT" };
    case "Skriv personlig intro": {
      return { ...state, skrivPersonligIntro: action.payload };
    }
    case "Venter på svar fra bruker": {
      return { ...state, venterPaaSvarFraBruker: action.payload };
    }
    case "Skriv personlig melding":
      return {
        ...state,
        skrivPersonligMelding: action.payload,
      };
    case "Reset":
      return initInitialState({
        originalHilsen: state.originalHilsen,
        deletekst: state.deletekst,
        introtekst: state.introtekst,
      });
  }
}

export function initInitialState(tekster: {
  deletekst: string;
  originalHilsen: string;
  introtekst: string;
}): State {
  return {
    deletekst: tekster.deletekst,
    originalHilsen: tekster.originalHilsen,
    hilsen: tekster.originalHilsen,
    sendtStatus: "IKKE_SENDT",
    dialogId: "",
    introtekst: tekster.introtekst,
    skrivPersonligIntro: false,
    skrivPersonligMelding: false,
    venterPaaSvarFraBruker: false,
  };
}

function sySammenIntroTekst(brukernavn?: string) {
  return `Hei ${brukernavn}\n`;
}

function sySammenBrukerTekst(
  chattekst: string,
  tiltaksgjennomforingsnavn: string,
  brukernavn?: string,
) {
  return `${chattekst
    .replaceAll("<Fornavn>", brukernavn ? `${brukernavn}` : "")
    .replaceAll("<tiltaksnavn>", tiltaksgjennomforingsnavn)}`;
}

function sySammenHilsenTekst(veiledernavn?: string) {
  const interessant = "Er dette aktuelt for deg? Gi meg tilbakemelding her i dialogen.";
  return veiledernavn
    ? `${interessant}\n\nVi holder kontakten!\nHilsen ${veiledernavn}`
    : `${interessant}\n\nVi holder kontakten!\nHilsen `;
}

const Delemodal = ({
  modalOpen,
  lukkModal,
  brukernavn,
  chattekst,
  veiledernavn,
  brukerFnr,
  tiltaksgjennomforing,
  brukerdata,
  harDeltMedBruker,
}: DelemodalProps) => {
  const introtekst = sySammenIntroTekst(brukernavn);
  const deletekst = sySammenBrukerTekst(chattekst, tiltaksgjennomforing.navn, brukernavn);
  const originalHilsen = sySammenHilsenTekst(veiledernavn);
  const [state, dispatch] = useReducer(
    reducer,
    { deletekst, originalHilsen, introtekst },
    initInitialState,
  );

  const senderTilDialogen = state.sendtStatus === "SENDER";
  const { lagreVeilederHarDeltTiltakMedBruker } = useHentDeltMedBrukerStatus(
    brukerFnr,
    tiltaksgjennomforing,
  );

  const clickCancel = (log = true) => {
    lukkModal();
    dispatch({ type: "Avbryt", payload: { tekster: { introtekst, deletekst, originalHilsen } } });
    log && logDelMedbrukerEvent("Avbrutt del med bruker");
  };

  const getAntallTegn = (tekst: string) => {
    return tekst.length;
  };

  const sySammenDeletekst = () => {
    return `${state.introtekst}${state.deletekst}\n\n${state.hilsen}`;
  };

  const handleSend = async () => {
    const { hilsen, introtekst, venterPaaSvarFraBruker } = state;
    if (
      hilsen.trim().length > getAntallTegn(hilsen) ||
      introtekst.length > getAntallTegn(introtekst)
    ) {
      return;
    }
    logDelMedbrukerEvent("Delte med bruker");

    dispatch({ type: "Send melding" });
    const overskrift = `Tiltak gjennom NAV: ${tiltaksgjennomforing.navn}`;
    const tekst = sySammenDeletekst();
    try {
      const res = await mulighetsrommetClient.dialogen.delMedDialogen({
        requestBody: {
          norskIdent: brukerFnr,
          overskrift,
          tekst,
          venterPaaSvarFraBruker,
        },
      });
      await lagreVeilederHarDeltTiltakMedBruker(res.id, tiltaksgjennomforing);
      dispatch({ type: "Sendt ok", payload: res.id });
    } catch {
      dispatch({ type: "Sending feilet" });
      logDelMedbrukerEvent("Del med bruker feilet");
    }
  };

  const feilmelding = utledFeilmelding(brukerdata);

  return (
    <>
      {feilmelding ? (
        <StatusModal
          modalOpen={modalOpen}
          onClose={lukkModal}
          ikonVariant="warning"
          heading={"Kunne ikke dele tiltaket"}
          text={feilmelding}
          primaryButtonText={"OK"}
          primaryButtonOnClick={() => lukkModal()}
        />
      ) : (
        <Modal
          open={modalOpen}
          onClose={() => clickCancel()}
          className={delemodalStyles.delemodal}
          aria-label="modal"
        >
          <Modal.Header closeButton data-testid="modal_header">
            <Heading size="xsmall">Del med bruker</Heading>
            <Heading size="large" level="1" className={delemodalStyles.heading}>
              {"Tiltak gjennom NAV: " + tiltaksgjennomforing.navn}
            </Heading>
          </Modal.Header>
          <Modal.Body>
            {state.sendtStatus !== "SENDT_OK" && state.sendtStatus !== "SENDING_FEILET" && (
              <>
                <DelMedBrukerContent
                  state={state}
                  dispatch={dispatch}
                  veiledernavn={veiledernavn}
                  brukernavn={brukernavn}
                  harDeltMedBruker={harDeltMedBruker}
                  tiltaksgjennomforing={tiltaksgjennomforing}
                />

                <Checkbox
                  onChange={(e) =>
                    dispatch({
                      type: "Venter på svar fra bruker",
                      payload: e.currentTarget.checked,
                    })
                  }
                  checked={state.venterPaaSvarFraBruker}
                  value="venter-pa-svar-fra-bruker"
                >
                  Venter på svar fra bruker
                </Checkbox>
              </>
            )}
          </Modal.Body>
          <Modal.Footer>
            <div className={modalStyles.knapperad}>
              <Button
                variant="tertiary"
                onClick={() => clickCancel(true)}
                data-testid="modal_btn-cancel"
                disabled={senderTilDialogen}
              >
                Avbryt
              </Button>
              <Button
                onClick={handleSend}
                data-testid="modal_btn-send"
                disabled={
                  senderTilDialogen ||
                  state.hilsen.length === 0 ||
                  state.hilsen.length > MAKS_ANTALL_TEGN_DEL_MED_BRUKER ||
                  state.introtekst.length === 0 ||
                  state.introtekst.length > MAKS_ANTALL_TEGN_DEL_MED_BRUKER ||
                  erPreview()
                }
              >
                {senderTilDialogen ? "Sender..." : "Send via Dialogen"}
              </Button>
            </div>
            <BodyShort size="small">
              Kandidatene vil få et varsel fra NAV, og kan logge inn på nav.no for å lese meldingen.
            </BodyShort>
          </Modal.Footer>
        </Modal>
      )}
      {state.sendtStatus === "SENDING_FEILET" && (
        <StatusModal
          modalOpen={modalOpen}
          ikonVariant="error"
          heading="Tiltaket kunne ikke deles"
          text={
            <>
              Tiltaket kunne ikke deles på grunn av en teknisk feil hos oss. Forsøk på nytt eller ta{" "}
              <a href={PORTEN}>kontakt i Porten</a> dersom du trenger mer hjelp.
            </>
          }
          onClose={clickCancel}
          primaryButtonOnClick={() => dispatch({ type: "Reset" })}
          primaryButtonText="Prøv igjen"
          secondaryButtonOnClick={clickCancel}
          secondaryButtonText="Avbryt"
        />
      )}
      {state.sendtStatus === "SENDT_OK" && (
        <StatusModal
          modalOpen={modalOpen}
          onClose={clickCancel}
          ikonVariant="success"
          heading="Tiltaket er delt med brukeren"
          text="Det er opprettet en ny tråd i Dialogen der du kan fortsette kommunikasjonen rundt dette tiltaket med brukeren."
          primaryButtonText="Gå til dialogen"
          primaryButtonOnClick={(event) => byttTilDialogFlate({ event, dialogId: state.dialogId })}
          secondaryButtonText="Lukk"
          secondaryButtonOnClick={() => clickCancel(false)}
        />
      )}
    </>
  );
};

function utledFeilmelding(brukerdata: Bruker) {
  if (!brukerdata.manuellStatus) {
    return "Vi kunne ikke opprette kontakt med KRR og vet derfor ikke om brukeren har reservert seg mot elektronisk kommunikasjon.";
  } else if (brukerdata.manuellStatus.erUnderManuellOppfolging) {
    return "Brukeren er under manuell oppfølging og kan derfor ikke benytte seg av våre digitale tjenester.";
  } else if (brukerdata.manuellStatus.krrStatus && brukerdata.manuellStatus.krrStatus.erReservert) {
    return "Brukeren har reservert seg mot elektronisk kommunikasjon i Kontakt- og reservasjonsregisteret (KRR).";
  } else if (brukerdata.manuellStatus.krrStatus && !brukerdata.manuellStatus.krrStatus.kanVarsles) {
    return "Brukeren er reservert mot elektronisk kommunikasjon i KRR. Vi kan derfor ikke kommunisere digitalt med denne brukeren.";
  } else {
    return null;
  }
}

export default Delemodal;
