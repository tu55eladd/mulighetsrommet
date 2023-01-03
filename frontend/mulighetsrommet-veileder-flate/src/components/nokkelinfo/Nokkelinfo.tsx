import { BodyShort, Heading, HelpText } from '@navikt/ds-react';
import { NokkelinfoKomponenter } from '../../core/api/models';
import styles from './Nokkelinfo.module.scss';

export interface NokkelinfoProps {
  nokkelinfoKomponenter: NokkelinfoKomponenter[];
  uuTitle?: string;
}

const Nokkelinfo = ({ nokkelinfoKomponenter, uuTitle, ...rest }: NokkelinfoProps) => {
  return (
    <div className={styles.container} {...rest}>
      {nokkelinfoKomponenter.map((nokkelinfo: NokkelinfoKomponenter, index: number) => {
        return (
          <div className={styles.nokkelinfo} key={index}>
            <div className={styles.content}>
              {typeof nokkelinfo.innhold === 'string' ? (
                <BodyShort className={styles.tekst}>{nokkelinfo.innhold}</BodyShort>
              ) : (
                <div className={styles.tekst}>{nokkelinfo.innhold}</div>
              )}

              {nokkelinfo.hjelpetekst && (
                <HelpText title={uuTitle} placement="right" style={{ maxWidth: '400px' }}>
                  {nokkelinfo.hjelpetekst}
                </HelpText>
              )}
            </div>
            <Heading className={styles.heading} size="xsmall" level="2">
              {nokkelinfo.tittel}
            </Heading>
          </div>
        );
      })}
    </div>
  );
};

export default Nokkelinfo;
