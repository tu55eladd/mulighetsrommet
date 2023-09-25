/* eslint-disable camelcase */
import { Tilgjengelighetsstatus } from "mulighetsrommet-api-client";
import StatusGronn from "../../ikoner/Sirkel-gronn.png";
import StatusGul from "../../ikoner/Sirkel-gul.png";
import StatusRod from "../../ikoner/Sirkel-rod.png";
import { formaterDato } from "../../utils/Utils";
import styles from "./Tilgjengelighetsstatus.module.scss";

interface Props {
  status?: Tilgjengelighetsstatus;
  estimertVentetid?: string;
  stengtFra?: string;
  stengtTil?: string;
}

function EstimertVentetid({ estimert_ventetid }: { estimert_ventetid?: string }) {
  return estimert_ventetid ? (
    <small className={styles.estimert_ventetid}>{estimert_ventetid}</small>
  ) : null;
}

export function TilgjengelighetsstatusComponent({
  status,
  estimertVentetid,
  stengtFra,
  stengtTil,
}: Props) {
  const todayDate = new Date();

  if (
    stengtFra &&
    stengtTil &&
    todayDate <= new Date(stengtTil) &&
    todayDate >= new Date(stengtFra)
  ) {
    return (
      <div>
        <div className={styles.tilgjengelighetsstatus}>
          <img
            src={StatusRod}
            alt="Rødt ikon som representerer at tiltaksgjennomføringen er stengt"
          />
          <div
            title={`Midlertidig stengt mellom ${formaterDato(stengtFra)} og ${formaterDato(
              stengtTil,
            )}`}
          >
            Midlertidig stengt
          </div>
        </div>
      </div>
    );
  }
  if (status === Tilgjengelighetsstatus.LEDIG || !status) {
    return (
      <div>
        <div className={styles.tilgjengelighetsstatus}>
          <img
            src={StatusGronn}
            alt="Grønt ikon som representerer at tilgjengelighetsstatus er åpent"
          />
          <div>Åpent</div>
        </div>
        <EstimertVentetid estimert_ventetid={estimertVentetid} />
      </div>
    );
  } else if (status === Tilgjengelighetsstatus.STENGT) {
    return (
      <div title={estimertVentetid ?? ""}>
        <div className={styles.tilgjengelighetsstatus}>
          <img
            src={StatusRod}
            alt="Rødt ikon som representerer at tilgjengelighetsstatus er stengt"
          />
          <div>Stengt</div>
        </div>
        <EstimertVentetid estimert_ventetid={estimertVentetid} />
      </div>
    );
  } else if (status === Tilgjengelighetsstatus.VENTELISTE) {
    return (
      <div title={estimertVentetid ?? ""}>
        <div className={styles.tilgjengelighetsstatus}>
          <img
            src={StatusGul}
            alt="Gult ikon som representerer at tilgjengelighetsstatus er venteliste"
          />
          <div>Venteliste</div>
        </div>
        <EstimertVentetid estimert_ventetid={estimertVentetid} />
      </div>
    );
  }

  return null;
}
