import React, { useState } from 'react';
import { Button, Panel } from '@navikt/ds-react';
import SidemenyAccordion from './SidemenyAccordion';
import './Sidemeny.less';
import Tilbakemeldingsmodal from '../modal/Tilbakemeldingsmodal';
import SendInformasjonModal from '../modal/SendInformasjonModal';

interface SidemenyProps {
  tiltaksnavn: string;
}

const SidemenyDetaljer = ({ tiltaksnavn }: SidemenyProps) => {
  const [tilbakemeldingsmodalOpen, setTilbakemeldingsmodalOpen] = useState(false);
  const [sendInformasjonModalOpen, setSendInformasjonModalOpen] = useState(false);

  return (
    <>
      <Panel className="tiltakstype-detaljer__sidemeny">
        <Button onClick={() => setSendInformasjonModalOpen(true)} data-testid="btn_send-informasjon">
          Send informasjon
        </Button>
        <Button variant="tertiary">Se ekstern nettside</Button>

        <SidemenyAccordion tittel="Kontaktinfo" isOpen={false}>
          Kontaktinfo
        </SidemenyAccordion>

        <SidemenyAccordion tittel="Dokumenter" isOpen={false}>
          Dokumenter
        </SidemenyAccordion>

        <Panel className="tiltakstype-detaljer__sidemeny__tilbakemelding">
          Har du forslag til forbedringer eller endringer vil vi gjerne at du sier ifra
          <Button onClick={() => setTilbakemeldingsmodalOpen(true)} data-testid="btn_gi-tilbakemelding">
            Gi tilbakemelding
          </Button>
        </Panel>
      </Panel>

      <Tilbakemeldingsmodal
        modalOpen={tilbakemeldingsmodalOpen}
        setModalOpen={() => setTilbakemeldingsmodalOpen(false)}
      />

      <SendInformasjonModal
        modalOpen={sendInformasjonModalOpen}
        setModalOpen={() => setSendInformasjonModalOpen(false)}
        tiltaksnavn={tiltaksnavn}
      />
    </>
  );
};

export default SidemenyDetaljer;
