import React from 'react';
import styles from './Detaljerfane.module.scss';

interface FaneMalTiltakProps {
  children: any;
  harInnhold: boolean;
}

const FaneTiltaksinformasjon = ({ children, harInnhold }: FaneMalTiltakProps) => {
  return <div className={styles.tiltaksdetaljer_maksbredde}>{harInnhold ? children : null}</div>;
};

export default FaneTiltaksinformasjon;
