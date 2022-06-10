import React from 'react';
import './TiltaksdetaljerFane.less';
import { Tabs } from '@navikt/ds-react';
import KontaktinfoFane from './kontaktinfofane/KontaktinfoFane';
import { Tiltaksansvarlig } from '../../../../mulighetsrommet-api-client/src/models/Tiltaksansvarlig';
import { Arrangor } from '../../../../mulighetsrommet-api-client/src/models/Arrangor';
import DetaljerFane from './detaljerFane';
import { Tiltakstype } from '../../../../mulighetsrommet-api-client';
import {logEvent} from "../../api/logger";

interface TiltaksdetaljerFaneProps {
  tiltaksgjennomforingTiltaksansvarlig: Tiltaksansvarlig;
  tiltaksgjennomforingArrangorinfo: Arrangor;
  tiltakstype: Tiltakstype;
  tiltaksgjennomforing: any;
}

const TiltaksdetaljerFane = ({
  tiltaksgjennomforingTiltaksansvarlig,
  tiltaksgjennomforingArrangorinfo,
  tiltakstype,
  tiltaksgjennomforing,
}: TiltaksdetaljerFaneProps) => {
  const faneoverskrifter = ['For hvem', 'Detaljer og innhold', 'Påmelding og varighet', 'Kontaktinfo', 'Innsikt'];

  return (
    <Tabs defaultValue="tab1" size="medium" selectionFollowsFocus className="fane__root" onChange={value => logEvent('mulighetsrommet.faner', {value})}>
      <Tabs.List loop className="fane__liste">
        {faneoverskrifter.map((fane, index) => (
          <Tabs.Tab key={index} value={`tab${index + 1}`} label={fane} className="btn__tab" />
        ))}
      </Tabs.List>
      <Tabs.Panel value="tab1">
        <DetaljerFane
          tiltaksgjennomforingAlert={tiltaksgjennomforing.forHvemInfoboks}
          tiltakstypeAlert={tiltakstype.faneinnhold!.forHvemInfoboks}
          tiltaksgjennomforing={tiltaksgjennomforing.forHvem}
          tiltakstype={tiltakstype.faneinnhold!.forHvem}
        />
      </Tabs.Panel>
      <Tabs.Panel value="tab2">
        <DetaljerFane
          tiltaksgjennomforingAlert={tiltaksgjennomforing.detaljerOgInnholdInfoboks}
          tiltakstypeAlert={tiltakstype.faneinnhold!.detaljerOgInnholdInfoboks}
          tiltaksgjennomforing={tiltaksgjennomforing.detaljerOgInnhold}
          tiltakstype={tiltakstype.faneinnhold!.detaljerOgInnhold}
        />
      </Tabs.Panel>
      <Tabs.Panel value="tab3">
        <DetaljerFane
          tiltaksgjennomforingAlert={tiltaksgjennomforing.pameldingOgVarighetInfoboks}
          tiltakstypeAlert={tiltakstype.faneinnhold!.pameldingOgVarighetInfoboks}
          tiltaksgjennomforing={tiltaksgjennomforing.pameldingOgVarighet}
          tiltakstype={tiltakstype.faneinnhold!.pameldingOgVarighet}
        />
      </Tabs.Panel>
      <Tabs.Panel value="tab4">
        <KontaktinfoFane
          tiltaksansvarligInfo={tiltaksgjennomforingTiltaksansvarlig}
          arrangorinfo={tiltaksgjennomforingArrangorinfo}
        />
      </Tabs.Panel>
      <Tabs.Panel value="tab5">Innsikt</Tabs.Panel>
    </Tabs>
  );
};

export default TiltaksdetaljerFane;
