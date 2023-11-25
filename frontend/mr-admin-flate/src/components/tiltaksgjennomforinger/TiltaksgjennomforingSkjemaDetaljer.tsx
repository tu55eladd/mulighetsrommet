import { PlusIcon, XMarkIcon } from "@navikt/aksel-icons";
import { Button, Checkbox, TextField } from "@navikt/ds-react";
import {
  Avtale,
  Tiltaksgjennomforing,
  TiltaksgjennomforingOppstartstype,
} from "mulighetsrommet-api-client";
import { ControlledSokeSelect } from "mulighetsrommet-frontend-common";
import { useFieldArray, useFormContext } from "react-hook-form";
import { useHentAnsatt } from "../../api/ansatt/useHentAnsatt";
import { useHentBetabrukere } from "../../api/ansatt/useHentBetabrukere";
import { useHentKontaktpersoner } from "../../api/ansatt/useHentKontaktpersoner";
import { useVirksomhet } from "../../api/virksomhet/useVirksomhet";
import { addYear, tilgjengelighetsstatusTilTekst } from "../../utils/Utils";
import { Separator } from "../detaljside/Metadata";
import { AdministratorOptions } from "../skjema/AdministratorOptions";
import { ControlledMultiSelect } from "../skjema/ControlledMultiSelect";
import { FormGroup } from "../skjema/FormGroup";
import { FraTilDatoVelger } from "../skjema/FraTilDatoVelger";
import skjemastyles from "../skjema/Skjema.module.scss";
import { VirksomhetKontaktpersoner } from "../virksomhet/VirksomhetKontaktpersoner";
import { arrangorUnderenheterOptions, erArenaOpphav } from "./TiltaksgjennomforingSkjemaConst";

interface Props {
  tiltaksgjennomforing?: Tiltaksgjennomforing;
  avtale: Avtale;
}

export const TiltaksgjennomforingSkjemaDetaljer = ({ tiltaksgjennomforing, avtale }: Props) => {
  const { data: virksomhet } = useVirksomhet(avtale.leverandor.organisasjonsnummer || "");
  const { data: betabrukere } = useHentBetabrukere();

  const { data: ansatt, isLoading: isLoadingAnsatt } = useHentAnsatt();

  const { data: kontaktpersoner, isLoading: isLoadingKontaktpersoner } = useHentKontaktpersoner();

  const kontaktpersonerOption = () => {
    const options = kontaktpersoner?.map((kontaktperson) => ({
      label: `${kontaktperson.fornavn} ${kontaktperson.etternavn} - ${kontaktperson.navIdent}`,
      value: kontaktperson.navIdent,
    }));

    return options || [];
  };

  const {
    register,
    control,
    formState: { errors },
    setValue,
    watch,
  } = useFormContext();
  const {
    fields: kontaktpersonFields,
    append: appendKontaktperson,
    remove: removeKontaktperson,
  } = useFieldArray({
    name: "kontaktpersoner",
    control,
  });

  const watchErMidlertidigStengt = watch("midlertidigStengt.erMidlertidigStengt");

  const regionerOptions = avtale.kontorstruktur
    .map((struk) => struk.region)
    .map((kontor) => ({ value: kontor.enhetsnummer, label: kontor.navn }));

  const navEnheterOptions = avtale.kontorstruktur
    .flatMap((struk) => struk.kontorer)
    .filter((kontor) => kontor.overordnetEnhet === watch("navRegion"))
    .map((kontor) => ({ label: kontor.navn, value: kontor.enhetsnummer }));

  const minStartdato = new Date();
  const maxSluttdato = addYear(minStartdato, 5);

  return (
    <div className={skjemastyles.container}>
      <div className={skjemastyles.input_container}>
        <div className={skjemastyles.column}>
          <FormGroup>
            <TextField
              size="small"
              readOnly={erArenaOpphav(tiltaksgjennomforing)}
              error={errors.navn?.message as string}
              label="Tiltaksnavn"
              autoFocus
              {...register("navn")}
            />
          </FormGroup>
          <Separator />
          <FormGroup>
            <TextField size="small" readOnly label={"Avtale"} value={avtale.navn || ""} />
          </FormGroup>
          <Separator />
          <FormGroup>
            <ControlledSokeSelect
              size="small"
              label="Oppstartstype"
              placeholder="Velg oppstart"
              {...register("oppstart")}
              options={[
                {
                  label: "Felles oppstartsdato",
                  value: TiltaksgjennomforingOppstartstype.FELLES,
                },
                {
                  label: "Løpende oppstart",
                  value: TiltaksgjennomforingOppstartstype.LOPENDE,
                },
              ]}
            />
            <FraTilDatoVelger
              size="small"
              fra={{
                label: "Startdato",
                readOnly: erArenaOpphav(tiltaksgjennomforing),
                fromDate: minStartdato,
                toDate: maxSluttdato,
                ...register("startOgSluttDato.startDato"),
                format: "iso-string",
              }}
              til={{
                label: "Sluttdato",
                readOnly: erArenaOpphav(tiltaksgjennomforing),
                fromDate: minStartdato,
                toDate: maxSluttdato,
                ...register("startOgSluttDato.sluttDato"),
                format: "iso-string",
              }}
            />
            <Checkbox
              size="small"
              readOnly={erArenaOpphav(tiltaksgjennomforing)}
              {...register("apenForInnsok")}
            >
              Åpen for innsøk
            </Checkbox>
            <Checkbox size="small" {...register("midlertidigStengt.erMidlertidigStengt")}>
              Midlertidig stengt
            </Checkbox>
            {watchErMidlertidigStengt && (
              <FraTilDatoVelger
                size="small"
                fra={{
                  label: "Stengt fra",
                  fromDate: minStartdato,
                  toDate: maxSluttdato,
                  ...register("midlertidigStengt.stengtFra"),
                  format: "date",
                }}
                til={{
                  label: "Stengt til",
                  fromDate: watch("midlertidigStengt.stengtFra") ?? new Date(),
                  toDate: maxSluttdato,
                  ...register("midlertidigStengt.stengtTil"),
                  format: "date",
                }}
              />
            )}
            <TextField
              size="small"
              readOnly={erArenaOpphav(tiltaksgjennomforing)}
              error={errors.antallPlasser?.message as string}
              type="number"
              style={{
                width: "180px",
              }}
              label="Antall plasser"
              {...register("antallPlasser", {
                valueAsNumber: true,
              })}
            />
          </FormGroup>
          <Separator />
          <FormGroup>
            <TextField
              readOnly
              size="small"
              label="Tilgjengelighetsstatus"
              value={tilgjengelighetsstatusTilTekst(tiltaksgjennomforing?.tilgjengelighet)}
            />
          </FormGroup>
          <Separator />
          <FormGroup>
            <ControlledMultiSelect
              size="small"
              placeholder={isLoadingAnsatt ? "Laster..." : "Velg en"}
              label={"Administratorer for gjennomføringen"}
              {...register("administratorer")}
              options={AdministratorOptions(
                ansatt,
                tiltaksgjennomforing?.administratorer,
                betabrukere,
              )}
            />
          </FormGroup>
        </div>
        <div className={skjemastyles.vertical_separator} />
        <div className={skjemastyles.column}>
          <div className={skjemastyles.gray_container}>
            <FormGroup>
              <ControlledSokeSelect
                size="small"
                label="NAV-region"
                placeholder="Velg en"
                {...register("navRegion")}
                onInputChange={() => {
                  setValue("navEnheter", []);
                }}
                options={regionerOptions}
              />
              <ControlledMultiSelect
                size="small"
                placeholder={"Velg en"}
                label={"NAV-enheter (kontorer)"}
                {...register("navEnheter")}
                options={navEnheterOptions}
              />
            </FormGroup>
            <Separator />
            <FormGroup>
              <div>
                {kontaktpersonFields?.map((field, index) => {
                  return (
                    <div className={skjemastyles.kontaktperson_container} key={field.id}>
                      <button
                        className={skjemastyles.kontaktperson_button}
                        type="button"
                        onClick={() => {
                          if (watch("kontaktpersoner")!.length > 1) {
                            removeKontaktperson(index);
                          } else {
                            setValue("kontaktpersoner", [
                              {
                                navIdent: "",
                                navEnheter: [],
                              },
                            ]);
                          }
                        }}
                      >
                        <XMarkIcon fontSize="1.5rem" />
                      </button>
                      <div className={skjemastyles.kontaktperson_inputs}>
                        <ControlledSokeSelect
                          size="small"
                          placeholder={
                            isLoadingKontaktpersoner ? "Laster kontaktpersoner..." : "Velg en"
                          }
                          label={"Kontaktperson i NAV"}
                          {...register(`kontaktpersoner.${index}.navIdent`, {
                            shouldUnregister: true,
                          })}
                          options={kontaktpersonerOption()}
                        />
                        <ControlledMultiSelect
                          size="small"
                          placeholder={isLoadingKontaktpersoner ? "Laster enheter..." : "Velg en"}
                          label={"Område"}
                          {...register(`kontaktpersoner.${index}.navEnheter`, {
                            shouldUnregister: true,
                          })}
                          options={navEnheterOptions}
                        />
                      </div>
                    </div>
                  );
                })}
                <Button
                  className={skjemastyles.kontaktperson_button}
                  type="button"
                  size="small"
                  onClick={() =>
                    appendKontaktperson({
                      navIdent: "",
                      navEnheter: [],
                    })
                  }
                >
                  <PlusIcon aria-label="Legg til ny kontaktperson" /> Legg til ny kontaktperson
                </Button>
              </div>
            </FormGroup>
          </div>
          <div className={skjemastyles.gray_container}>
            <FormGroup>
              <TextField
                size="small"
                label="Tiltaksarrangør hovedenhet"
                placeholder=""
                defaultValue={`${avtale.leverandor.navn} - ${avtale.leverandor.organisasjonsnummer}`}
                readOnly
              />
              <ControlledSokeSelect
                size="small"
                label="Tiltaksarrangør underenhet"
                placeholder="Velg underenhet for tiltaksarrangør"
                {...register("tiltaksArrangorUnderenhetOrganisasjonsnummer")}
                onChange={() => {
                  setValue("arrangorKontaktpersonId", null);
                }}
                onClearValue={() => {
                  setValue("tiltaksArrangorUnderenhetOrganisasjonsnummer", "");
                }}
                readOnly={
                  !avtale.leverandor.organisasjonsnummer || erArenaOpphav(tiltaksgjennomforing)
                }
                options={arrangorUnderenheterOptions(avtale, virksomhet)}
              />
              {watch("tiltaksArrangorUnderenhetOrganisasjonsnummer") &&
                !tiltaksgjennomforing?.arrangor?.slettet && (
                  <div className={skjemastyles.virksomhet_kontaktperson_container}>
                    <VirksomhetKontaktpersoner
                      title={"Kontaktperson hos arrangøren"}
                      orgnr={watch("tiltaksArrangorUnderenhetOrganisasjonsnummer")}
                      formValueName={"arrangorKontaktpersonId"}
                    />
                  </div>
                )}
              <TextField
                size="small"
                label="Sted for gjennomføring"
                description="Skriv inn stedet tiltaket skal gjennomføres, for eksempel Fredrikstad eller Tromsø. For tiltak uten eksplisitt lokasjon (for eksempel digital jobbklubb), kan du la feltet stå tomt."
                {...register("stedForGjennomforing")}
                error={
                  errors.stedForGjennomforing
                    ? (errors.stedForGjennomforing.message as string)
                    : null
                }
              />
            </FormGroup>
          </div>
        </div>
      </div>
    </div>
  );
};
