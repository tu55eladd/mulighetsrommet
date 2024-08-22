import { zodResolver } from "@hookform/resolvers/zod";
import { FormProvider, useForm } from "react-hook-form";
import { InferredOpprettTilsagnSchema, OpprettTilsagnSchema } from "./OpprettTilsagnSchema";
import { useEffect } from "react";
import { HGrid, TextField, DatePicker, BodyShort, Alert, HStack, Button } from "@navikt/ds-react";
import { addYear } from "../../utils/Utils";
import { ControlledDateInput } from "../skjema/ControlledDateInput";
import { FormGroup } from "../skjema/FormGroup";
import { ApiError, TilsagnDto, TilsagnRequest, Tiltaksgjennomforing } from "@mr/api-client";
import { useNavEnheter } from "../../api/enhet/useNavEnheter";
import { UseMutationResult } from "@tanstack/react-query";
import { AFTBeregningSkjema } from "./AFTBeregningSkjema";
import { FriBeregningSkjema } from "./FriBeregningSkjema";
import { ControlledSokeSelect } from "@mr/frontend-common";

interface Props {
  tiltaksgjennomforing: Tiltaksgjennomforing;
  tilsagn?: TilsagnDto;
  onSubmit: (data: InferredOpprettTilsagnSchema) => void;
  onAvbryt?: () => void;
  mutation: UseMutationResult<TilsagnDto, ApiError, TilsagnRequest, unknown>;
  prismodell: "AFT" | "FRI";
}

export function TilsagnSkjema({
  tiltaksgjennomforing,
  tilsagn,
  onSubmit,
  onAvbryt,
  mutation,
  prismodell,
}: Props) {
  const { data: navEnheter } = useNavEnheter();

  const form = useForm<InferredOpprettTilsagnSchema>({
    resolver: zodResolver(OpprettTilsagnSchema),
    defaultValues: tilsagn
      ? {
          id: tilsagn.id,
          beregning: tilsagn.beregning,
          kostnadssted: tilsagn.kostnadssted.enhetsnummer,
          periode: {
            start: tilsagn.periodeStart,
            slutt: tilsagn.periodeSlutt,
          },
        }
      : {},
  });

  const { handleSubmit, register, setValue } = form;

  useEffect(() => {
    if (tilsagn) {
      setValue("id", tilsagn.id);
      setValue("kostnadssted", tilsagn?.kostnadssted.enhetsnummer);
      setValue("beregning", tilsagn.beregning);
      setValue("periode.start", tilsagn.periodeStart);
      setValue("periode.slutt", tilsagn.periodeSlutt);
    }
  }, [navEnheter, tilsagn, setValue]);

  return (
    <FormProvider {...form}>
      <form onSubmit={handleSubmit(onSubmit)}>
        <FormGroup>
          <HGrid columns={2} gap="2">
            <TextField
              size="small"
              readOnly
              label="Tiltaksgjennomføring"
              value={tiltaksgjennomforing.navn}
            />
            <TextField
              readOnly
              size="small"
              label="Organisasjonsnummer for arrangør"
              value={`${tiltaksgjennomforing.arrangor.navn} - ${tiltaksgjennomforing.arrangor.organisasjonsnummer}`}
            />
          </HGrid>
        </FormGroup>

        <FormGroup>
          <DatePicker>
            <HGrid columns={2} gap={"2"}>
              <ControlledDateInput
                label="Startdato"
                fromDate={new Date(tiltaksgjennomforing.startDato)}
                toDate={addYear(new Date(), 50)}
                format="iso-string"
                {...register("periode.start")}
                size="small"
              />
              <ControlledDateInput
                label="Sluttdato"
                fromDate={new Date(tiltaksgjennomforing.startDato)}
                toDate={addYear(new Date(), 50)}
                format="iso-string"
                {...register("periode.slutt")}
                size="small"
              />
            </HGrid>
          </DatePicker>
        </FormGroup>
        <FormGroup>
          {prismodell == "AFT" ? (
            <AFTBeregningSkjema defaultAntallPlasser={tiltaksgjennomforing.antallPlasser} />
          ) : (
            <FriBeregningSkjema />
          )}
        </FormGroup>
        <FormGroup>
          <ControlledSokeSelect
            placeholder="Velg kostnadssted"
            size="small"
            label="Kostnadssted"
            {...register("kostnadssted")}
            options={
              navEnheter
                ?.sort((a, b) => a.navn.localeCompare(b.navn))
                .map(({ navn, enhetsnummer }) => {
                  return {
                    value: enhetsnummer,
                    label: `${navn} - ${enhetsnummer}`,
                  };
                }) ?? []
            }
          />
        </FormGroup>
        <BodyShort spacing>
          {mutation.error ? (
            <Alert variant="error" size="small">
              Klarte ikke opprette tilsagn
            </Alert>
          ) : null}
        </BodyShort>
        <HStack gap="2" justify={"space-between"}>
          <Button size="small" type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? "Sender til beslutning" : "Send til beslutning"}
          </Button>

          <Button onClick={onAvbryt} size="small" type="button" variant="primary-neutral">
            Avbryt
          </Button>
        </HStack>
      </form>
    </FormProvider>
  );
}
