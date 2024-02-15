import { Button, Textarea, TextField, VStack } from "@navikt/ds-react";
import {
  Avtale,
  Avtaletype,
  EmbeddedTiltakstype,
  NavAnsatt,
  NavEnhet,
  NavEnhetType,
  Opphav,
  Tiltakstype,
} from "mulighetsrommet-api-client";
import { ControlledSokeSelect } from "mulighetsrommet-frontend-common/components/ControlledSokeSelect";
import { SelectOption } from "mulighetsrommet-frontend-common/components/SokeSelect";
import { useRef, useState } from "react";
import { useFormContext } from "react-hook-form";
import { MultiValue } from "react-select";
import { useAvtaleAdministratorer } from "../../api/ansatt/useAvtaleAdministratorer";
import { useSokVirksomheter } from "../../api/virksomhet/useSokVirksomhet";
import { useVirksomhet } from "../../api/virksomhet/useVirksomhet";
import { erAnskaffetTiltak } from "../../utils/tiltakskoder";
import { addYear } from "../../utils/Utils";
import { Separator } from "../detaljside/Metadata";
import { AdministratorOptions } from "../skjema/AdministratorOptions";
import { ControlledMultiSelect } from "../skjema/ControlledMultiSelect";
import { FormGroup } from "../skjema/FormGroup";
import { FraTilDatoVelger } from "../skjema/FraTilDatoVelger";
import skjemastyles from "../skjema/Skjema.module.scss";
import { VirksomhetKontaktpersonerModal } from "../virksomhet/VirksomhetKontaktpersonerModal";
import { InferredAvtaleSchema } from "./AvtaleSchema";
import { getLokaleUnderenheterAsSelectOptions, underenheterOptions } from "./AvtaleSkjemaConst";
import { useVirksomhetKontaktpersoner } from "../../api/virksomhet/useVirksomhetKontaktpersoner";
import { useMigrerteTiltakstyper } from "../../api/tiltakstyper/useMigrerteTiltakstyper";

const minStartdato = new Date(2000, 0, 1);

interface Props {
  tiltakstyper: Tiltakstype[];
  ansatt: NavAnsatt;
  avtale?: Avtale;
  enheter: NavEnhet[];
}

export function AvtaleSkjemaDetaljer({ tiltakstyper, ansatt, enheter, avtale }: Props) {
  const [sokLeverandor, setSokLeverandor] = useState("");
  const { data: leverandorVirksomheter = [] } = useSokVirksomheter(sokLeverandor);
  const { data: migrerteTiltakstyper } = useMigrerteTiltakstyper();

  const { data: administratorer } = useAvtaleAdministratorer();
  const virksomhetKontaktpersonerModalRef = useRef<HTMLDialogElement>(null);

  const {
    register,
    formState: { errors },
    watch,
    setValue,
  } = useFormContext<InferredAvtaleSchema>();

  const watchedTiltakstype: EmbeddedTiltakstype | undefined = watch("tiltakstype");
  const arenaKode = watchedTiltakstype?.arenaKode;

  const watchedLeverandor = watch("leverandor");
  const { data: virksomhetKontaktpersoner, refetch: refetchVirksomhetKontaktpersoner } =
    useVirksomhetKontaktpersoner(watchedLeverandor);
  const { data: leverandorData } = useVirksomhet(watchedLeverandor);

  const underenheterForLeverandor = leverandorData?.underenheter ?? [];

  const arenaOpphavOgIngenEierskap =
    avtale?.opphav === Opphav.ARENA &&
    !migrerteTiltakstyper?.includes(watchedTiltakstype.arenaKode);

  const navRegionerOptions = enheter
    .filter((enhet) => enhet.type === NavEnhetType.FYLKE)
    .map((enhet) => ({
      value: enhet.enhetsnummer,
      label: enhet.navn,
    }));

  const { startDato } = watch("startOgSluttDato");
  const sluttDatoFraDato = startDato ? new Date(startDato) : minStartdato;
  const sluttDatoTilDato = addYear(startDato ? new Date(startDato) : new Date(), 5);

  const leverandorOptions = () => {
    const options = leverandorVirksomheter.map((enhet) => ({
      value: enhet.organisasjonsnummer,
      label: `${enhet.navn} - ${enhet.organisasjonsnummer}`,
    }));

    if (leverandorData) {
      options.push({
        label: `${leverandorData.navn} - ${leverandorData.organisasjonsnummer}`,
        value: leverandorData.organisasjonsnummer,
      });
    } else if (watchedLeverandor) {
      // TODO Dette fører til flickering i gui. Hadde vært bedre om valgt leverandør alltid var tilgjengelig..
      options.push({ label: watchedLeverandor, value: watchedLeverandor });
    }

    return options;
  };

  return (
    <div className={skjemastyles.container}>
      <div className={skjemastyles.input_container}>
        <div className={skjemastyles.column}>
          <FormGroup cols={avtale?.avtalenummer ? 2 : 1}>
            <TextField
              size="small"
              readOnly={arenaOpphavOgIngenEierskap}
              error={errors.navn?.message}
              label="Avtalenavn"
              autoFocus
              {...register("navn")}
            />
            {avtale?.avtalenummer ? (
              <TextField size="small" readOnly label="Avtalenummer" value={avtale.avtalenummer} />
            ) : null}
          </FormGroup>
          <Separator />
          <FormGroup cols={2}>
            <ControlledSokeSelect
              size="small"
              readOnly={arenaOpphavOgIngenEierskap}
              placeholder="Velg en"
              label={"Tiltakstype"}
              {...register("tiltakstype")}
              options={tiltakstyper.map((tiltakstype) => ({
                value: {
                  arenaKode: tiltakstype.arenaKode,
                  navn: tiltakstype.navn,
                  id: tiltakstype.id,
                },
                label: tiltakstype.navn,
              }))}
            />
            <ControlledSokeSelect
              size="small"
              readOnly={arenaOpphavOgIngenEierskap}
              placeholder="Velg en"
              label={"Avtaletype"}
              {...register("avtaletype")}
              options={[
                {
                  value: Avtaletype.FORHAANDSGODKJENT,
                  label: "Forhåndsgodkjent avtale",
                },
                {
                  value: Avtaletype.RAMMEAVTALE,
                  label: "Rammeavtale",
                },
                {
                  value: Avtaletype.AVTALE,
                  label: "Avtale",
                },
              ]}
            />
          </FormGroup>
          <Separator />
          <FormGroup>
            <FraTilDatoVelger
              size="small"
              fra={{
                label: "Startdato",
                readOnly: arenaOpphavOgIngenEierskap,
                fromDate: minStartdato,
                toDate: sluttDatoTilDato,
                ...register("startOgSluttDato.startDato"),
                format: "iso-string",
              }}
              til={{
                label: "Sluttdato",
                readOnly: arenaOpphavOgIngenEierskap,
                fromDate: sluttDatoFraDato,
                toDate: sluttDatoTilDato,
                ...register("startOgSluttDato.sluttDato"),
                format: "iso-string",
              }}
            />
          </FormGroup>
          <Separator />
          <FormGroup>
            <TextField
              size="small"
              error={errors.url?.message}
              label="URL til avtale i Websak"
              {...register("url")}
            />
          </FormGroup>
          <Separator />
          {arenaKode && erAnskaffetTiltak(arenaKode) && (
            <>
              <FormGroup>
                <Textarea
                  size="small"
                  readOnly={arenaOpphavOgIngenEierskap}
                  error={errors.prisbetingelser?.message}
                  label="Pris og betalingsinformasjon"
                  {...register("prisbetingelser")}
                />
              </FormGroup>
              <Separator />
            </>
          )}
          <FormGroup>
            <ControlledMultiSelect
              size="small"
              helpText="Bestemmer hvem som eier avtalen. Notifikasjoner sendes til administratorene."
              placeholder="Administratorer"
              label="Administratorer for avtalen"
              {...register("administratorer")}
              options={AdministratorOptions(ansatt, avtale?.administratorer, administratorer)}
            />
          </FormGroup>
        </div>
        <div className={skjemastyles.vertical_separator} />
        <div className={skjemastyles.column}>
          <div className={skjemastyles.gray_container}>
            <FormGroup>
              <ControlledMultiSelect
                size="small"
                placeholder="Velg en"
                label={"NAV-regioner"}
                {...register("navRegioner")}
                additionalOnChange={(selectedOptions) => {
                  if (watch("navRegioner").length > 1) {
                    const alleLokaleUnderenheter = velgAlleLokaleUnderenheter(
                      selectedOptions,
                      enheter,
                    );
                    setValue("navEnheter", alleLokaleUnderenheter as [string, ...string[]]);
                  } else {
                    const alleLokaleUnderenheter = velgAlleLokaleUnderenheter(
                      selectedOptions,
                      enheter,
                    );
                    const navEnheter = watch("navEnheter").filter((enhet) =>
                      alleLokaleUnderenheter.includes(enhet),
                    );
                    setValue("navEnheter", navEnheter as [string, ...string[]]);
                  }
                }}
                options={navRegionerOptions}
              />
              <ControlledMultiSelect
                size="small"
                placeholder="Velg en"
                label={"NAV-enheter (kontorer)"}
                helpText="Bestemmer hvilke NAV-enheter som kan velges i gjennomføringene til avtalen."
                {...register("navEnheter")}
                options={getLokaleUnderenheterAsSelectOptions(watch("navRegioner"), enheter)}
              />
            </FormGroup>
          </div>
          <div className={skjemastyles.gray_container}>
            <FormGroup>
              <ControlledSokeSelect
                size="small"
                readOnly={arenaOpphavOgIngenEierskap}
                placeholder="Skriv for å søke etter tiltaksarrangør"
                label={"Tiltaksarrangør hovedenhet"}
                {...register("leverandor")}
                onInputChange={(value) => {
                  setSokLeverandor(value);
                }}
                onClearValue={() => setValue("leverandor", "")}
                options={leverandorOptions()}
              />
              <ControlledMultiSelect
                size="small"
                placeholder="Velg underenhet for tiltaksarrangør"
                label={"Tiltaksarrangør underenhet"}
                helpText="Bestemmer hvilke arrangører som kan velges i gjennomføringene til avtalen."
                readOnly={!watchedLeverandor}
                {...register("leverandorUnderenheter")}
                options={underenheterOptions(underenheterForLeverandor)}
              />
            </FormGroup>
            {watchedLeverandor && !avtale?.leverandor?.slettet && (
              <FormGroup>
                <VStack gap="2" className={skjemastyles.kontaktperson_container}>
                  <ControlledSokeSelect
                    size="small"
                    placeholder="Velg en"
                    label={"Kontaktperson hos leverandør"}
                    {...register("leverandorKontaktpersonId")}
                    options={
                      virksomhetKontaktpersoner?.map((person) => ({
                        value: person.id,
                        label: person.navn,
                      })) ?? []
                    }
                  />
                  <Button
                    size="small"
                    type="button"
                    variant="secondary"
                    onClick={() => virksomhetKontaktpersonerModalRef.current?.showModal()}
                  >
                    Rediger kontaktpersoner
                  </Button>
                </VStack>
              </FormGroup>
            )}
          </div>
        </div>
      </div>
      {watchedLeverandor && (
        <VirksomhetKontaktpersonerModal
          orgnr={watchedLeverandor}
          modalRef={virksomhetKontaktpersonerModalRef}
          onClose={() => {
            refetchVirksomhetKontaktpersoner().then((res) => {
              if (!res?.data?.some((p) => p.id === watch("leverandorKontaktpersonId"))) {
                setValue("leverandorKontaktpersonId", undefined);
              }
            });
          }}
        />
      )}
    </div>
  );
}

function velgAlleLokaleUnderenheter(
  selectedOptions: MultiValue<SelectOption<string>>,
  enheter: NavEnhet[],
): string[] {
  const regioner = selectedOptions?.map((option) => option.value);
  const navEnheter = getLokaleUnderenheterAsSelectOptions(regioner, enheter).map(
    (option) => option.value,
  );
  return navEnheter;
}
