import styles from "../notater/Notater.module.scss";
import {
  Button,
  Checkbox,
  ErrorMessage,
  Heading,
  Textarea,
} from "@navikt/ds-react";
import { useState } from "react";
import { FormProvider, SubmitHandler, useForm } from "react-hook-form";
import { AvtaleNotatRequest } from "mulighetsrommet-api-client";
import { useAvtale } from "../../api/avtaler/useAvtale";
import { v4 as uuidv4 } from "uuid";
import { zodResolver } from "@hookform/resolvers/zod";
import invariant from "tiny-invariant";
import { Laster } from "../laster/Laster";
import { useMineAvtalenotater } from "../../api/notater/avtalenotat/useMineAvtalenotater";
import { inferredNotatSchema, NotatSchema } from "../notater/NotatSchema";
import { useAvtalenotater } from "../../api/notater/avtalenotat/useAvtalenotater";
import { usePutAvtalenotat } from "../../api/notater/avtalenotat/usePutAvtalenotat";
import Notatliste from "../notater/Notatliste";
import { useDeleteAvtalenotat } from "../../api/notater/avtalenotat/useDeleteAvtalenotat";

export default function NotaterAvtalePage() {
  const { data: notater = [] } = useAvtalenotater();
  const { data: mineNotater = [] } = useMineAvtalenotater();
  const { data: avtaleData } = useAvtale();

  const mutation = usePutAvtalenotat();
  const [visMineNotater, setVisMineNotater] = useState(false);
  const liste = visMineNotater ? mineNotater : notater;

  const form = useForm<inferredNotatSchema>({
    resolver: zodResolver(NotatSchema),
    defaultValues: {
      innhold: "",
    },
  });

  const {
    handleSubmit,
    formState: { errors },
    register,
    reset,
    watch,
  } = form;

  const postData: SubmitHandler<inferredNotatSchema> = async (
    data,
  ): Promise<void> => {
    const { innhold } = data;
    invariant(avtaleData, "Klarte ikke hente avtale.");

    const requestBody: AvtaleNotatRequest = {
      id: uuidv4(),
      avtaleId: avtaleData.id,
      innhold,
    };

    mutation.mutate(requestBody, { onSuccess: () => reset() });
  };

  return (
    <div className={styles.notater}>
      <FormProvider {...form}>
        <form onSubmit={handleSubmit(postData)}>
          <div className={styles.notater_opprett}>
            <Textarea
              label={"Innhold for notat"}
              hideLabel
              className={styles.notater_input}
              error={errors.innhold?.message}
              minRows={10}
              maxRows={25}
              resize
              maxLength={500}
              {...register("innhold")}
              value={watch("innhold")}
              data-testId="notater_innhold"
            />
            {mutation.isError ? (
              <ErrorMessage>
                Det skjedde en feil. Notatet ble ikke lagret.
              </ErrorMessage>
            ) : null}
            <span className={styles.notater_knapp}>
              <Button
                type="submit"
                disabled={mutation.isLoading}
                data-testId="notater_legg-til-knapp"
              >
                {mutation.isLoading ? <Laster /> : "Legg til notat"}
              </Button>
            </span>
          </div>
        </form>
      </FormProvider>

      <div className={styles.notater_notatvegg}>
        <Heading size="medium" level="3" className={styles.notater_heading}>
          Notater
        </Heading>

        <div className={styles.notater_andrerad}>
          <Checkbox
            onChange={() => setVisMineNotater(!visMineNotater)}
            data-testid="vis-mine-notater"
          >
            Vis kun mine notater
          </Checkbox>
        </div>

        <Notatliste
          notater={liste}
          visMineNotater={visMineNotater}
          mutation={useDeleteAvtalenotat()}
        />
      </div>
    </div>
  );
}