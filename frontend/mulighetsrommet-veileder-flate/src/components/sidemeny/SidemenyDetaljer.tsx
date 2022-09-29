import { Panel } from '@navikt/ds-react';
import { Tiltaksgjennomforing } from '../../core/api/models';
import useTiltaksgjennomforingByTiltaksnummer from '../../core/api/queries/useTiltaksgjennomforingByTiltaksnummer';
import Kopiknapp from '../kopiknapp/Kopiknapp';
import Regelverksinfo from './Regelverksinfo';

const SidemenyDetaljer = () => {
  const { data } = useTiltaksgjennomforingByTiltaksnummer();
  if (!data) return null;

  const { tiltaksnummer, kontaktinfoArrangor, tiltakstype } = data;
  const oppstart = resolveOppstart(data);

  return (
    <>
      <Panel className="tiltakstype-detaljer__sidemeny">
        <div className="tiltakstype-detaljer__rad">
          <strong>Tiltaksnummer</strong>
          <div className="tiltakstype-detaljer__rad__info">
            {tiltaksnummer} <Kopiknapp kopitekst={String(tiltaksnummer)} dataTestId="knapp_kopier" />
          </div>
        </div>

        <div className="tiltakstype-detaljer__rad">
          <strong>Tiltakstype</strong>
          <span>{tiltakstype.tiltakstypeNavn}</span>
        </div>

        {kontaktinfoArrangor?.selskapsnavn ? (
          <div className="tiltakstype-detaljer__rad">
            <strong>Arrangør</strong>
            <span>{kontaktinfoArrangor.selskapsnavn}</span>
          </div>
        ) : null}

        <div className="tiltakstype-detaljer__rad">
          <strong>Innsatsgruppe</strong>
          <span>{tiltakstype?.innsatsgruppe?.beskrivelse} </span>
        </div>

        <div className="tiltakstype-detaljer__rad">
          <strong>Oppstart</strong>
          <span>{oppstart}</span>
        </div>
        {(tiltakstype.regelverkFiler || tiltakstype.regelverkLenker) && (
          <div className="tiltakstype-detaljer__rad">
            <strong>Regelverk</strong>
            <Regelverksinfo regelverkFiler={tiltakstype.regelverkFiler} regelverkLenker={tiltakstype.regelverkLenker} />
          </div>
        )}
      </Panel>
    </>
  );
};

function resolveOppstart({ oppstart, oppstartsdato }: Tiltaksgjennomforing) {
  if (oppstart === 'midlertidig_stengt') return 'Midlertidig stengt';

  return oppstart === 'dato' && oppstartsdato ? new Intl.DateTimeFormat().format(new Date(oppstartsdato)) : 'Løpende';
}

export default SidemenyDetaljer;
