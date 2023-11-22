import { Alert } from "@navikt/ds-react";
import { Oppskrift, Toggles } from "mulighetsrommet-api-client";
import { Link, Outlet } from "react-router-dom";
import { useFeatureToggle } from "../../core/api/feature-toggles";
import styles from "./Oppskriftsoversikt.module.scss";
import { formaterDato } from "../../utils/Utils";

interface Props {
  oppskrifter: Oppskrift[];
}

export function Oppskriftsoversikt({ oppskrifter }: Props) {
  const { data: enableOppskrifter } = useFeatureToggle(
    Toggles.MULIGHETSROMMET_VEILEDERFLATE_ARENA_OPPSKRIFTER,
  );

  if (!enableOppskrifter) return null;

  if (oppskrifter.length === 0) {
    return <Alert variant="info">Det er ikke lagt inn oppskrifter for denne tiltakstypen</Alert>;
  }

  return (
    <>
      <ul className={styles.container}>
        {oppskrifter.map((oppskrift) => {
          return (
            <li className={styles.item} key={oppskrift.navn}>
              <Link className={styles.link} to={`oppskrifter/${oppskrift._id}`}>
                <Oppskriftskort oppskrift={oppskrift} />
              </Link>
            </li>
          );
        })}
      </ul>
      <hr />
      <Outlet />
    </>
  );
}

interface OppskriftKortProps {
  oppskrift: Oppskrift;
}

function Oppskriftskort({ oppskrift: { navn, beskrivelse, _updatedAt } }: OppskriftKortProps) {
  return (
    <div className={styles.kort}>
      <div>
        <h3>{navn}</h3>
        <p>{beskrivelse}</p>
      </div>
      <small>Oppdatert: {formaterDato(new Date(_updatedAt))}</small>
    </div>
  );
}
