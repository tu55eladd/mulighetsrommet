import { PlusIcon } from "@navikt/aksel-icons";
import { Button, TextField } from "@navikt/ds-react";
import classNames from "classnames";
import { useEffect, useState } from "react";
import { v4 as uuidv4 } from "uuid";
import { useVirksomhetKontaktpersoner } from "../../api/virksomhet/useVirksomhetKontaktpersoner";
import styles from "./VirksomhetKontaktpersoner.module.scss";
import { usePutVirksomhetKontaktperson } from "../../api/virksomhet/usePutVirksomhetKontaktperson";
import { SokeSelect } from "../skjema/SokeSelect";
import { useFormContext } from "react-hook-form";
import { Laster } from "../laster/Laster";

interface State {
  leggTil: boolean
  navn?: string;
  epost?: string;
  telefon?: string;
  navnError?: string;
  epostError?: string;
  telefonError?: string;
}

interface VirksomhetKontaktpersonerProps {
  orgnr: string;
  formValueName: string;
}

export const VirksomhetKontaktpersoner = (
  props: VirksomhetKontaktpersonerProps
) => {
  const { orgnr, formValueName } = props;
  const { register, watch, setValue } = useFormContext();
  const mutation = usePutVirksomhetKontaktperson(orgnr);
  const {
    data: kontaktpersoner,
    isLoading: isLoadingKontaktpersoner,
    refetch
  } = useVirksomhetKontaktpersoner(orgnr);

  const initialState: State = {
    leggTil: false,
    navn: undefined,
    telefon: undefined,
    epost: undefined,
    navnError: undefined,
    telefonError: undefined,
    epostError: undefined,
  }

  const [state, setState] = useState<State>(initialState);

  useEffect(() => {
    if (mutation.isSuccess) {
      setValue(formValueName, mutation.data.id)
      setState(initialState);
      refetch();
      mutation.reset();
    }
  }, [mutation])

  if (!kontaktpersoner || isLoadingKontaktpersoner) {
    return <Laster />
  }

  const person = kontaktpersoner.find((person) => person.id === watch(formValueName))

  const opprettKontaktperson = () => {
    setState({
      ...state,
      navnError: !state.navn ? "Navn må være satt" : undefined,
      epostError: !state.epost ? "Epost må være satt" : undefined,
      telefonError: !state.telefon ? "Telefon må være satt" : undefined,
    });
    if (!state.navn || !state.epost || !state.telefon) {
      return;
    }

    mutation.mutate({
      id: uuidv4(),
      navn: state.navn,
      telefon: state.telefon,
      epost: state.epost,
    });
  };

  return (
    <>
      <div className={styles.kontaktperson_container}>
        <label className={styles.kontaktperson_label} >
          <b>Kontaktperson hos leverandøren</b>
        </label>
        <SokeSelect
          size="small"
          placeholder="Søk etter kontaktpersoner"
          label={"Lagrede kontaktpersoner"}
          {...register(formValueName)}
          options={kontaktpersoner.map((person) => ({
            value: person.id,
            label: person.navn,
          }))}
        />
        {person &&
          <div className={styles.kontaktperson_info_container}>
            <label>{`Navn: ${person.navn}`}</label>
            <label>{`Telefon: ${person.telefon}`}</label>
            <label>{`Epost: ${person.epost}`}</label>
          </div>
        }
        {!state.leggTil &&
          <Button
            className={classNames(
              styles.kontaktperson_button,
              styles.kontaktperson_fjern_button
            )}
            type="button"
            onClick={() => setState({ ...state, leggTil: !state.leggTil })}
          >
            <PlusIcon /> Legg til kontaktperson
          </Button>
        }
        {state.leggTil &&
          <div className={styles.input_container}>
            <TextField
              size="small"
              label={"Navn"}
              error={state.navnError}
              onChange={(e) => {
                setState({ ...state, navn: e.target.value, navnError: undefined });
              }}
            />
            <div className={styles.telefonepost_input}>
              <div className={styles.telefon_input}>
                <TextField
                  size="small"
                  label="Telefon"
                  error={state.telefonError}
                  onChange={(e) => {
                    setState({ ...state, telefon: e.target.value, telefonError: undefined });
                  }}
                />
              </div>
              <div className={styles.epost_input}>
                <TextField
                  size="small"
                  label="Epost"
                  error={state.epostError}
                  onChange={(e) => {
                    setState({ ...state, epost: e.target.value, epostError: undefined });
                  }}
                />
              </div>
            </div>
            <div className={styles.button_container}>
              <Button
                size="small"
                className={styles.button}
                type="button"
                onClick={opprettKontaktperson}
              >
                Opprett kontaktperson
              </Button>
              <Button
                size="small"
                variant="secondary"
                className={styles.button}
                type="button"
                onClick={() => setState(initialState)}
              >
                Avbryt
              </Button>
            </div>
          </div>
        }
      </div>
    </>
  );
}