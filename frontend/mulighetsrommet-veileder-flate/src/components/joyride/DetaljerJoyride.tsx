import Joyride, { ACTIONS, CallBackProps, EVENTS, STATUS } from 'react-joyride';
import { locale, styling } from './config';
import { JoyrideKnapp } from './JoyrideKnapp';
import { logEvent } from '../../core/api/logger';
import { detaljerSteps, isStep, useSteps } from './Steps';
import { useAtom } from 'jotai';
import { joyrideAtom } from '../../core/atoms/atoms';
import styles from './Joyride.module.scss';

interface Props {
  opprettAvtale: boolean;
}

export function DetaljerJoyride({ opprettAvtale }: Props) {
  const [joyride, setJoyride] = useAtom(joyrideAtom);

  const { steps, stepIndex, setStepIndex } = useSteps(joyride.joyrideDetaljer, detaljerSteps);

  const handleJoyrideCallback = (data: CallBackProps) => {
    const { action, index, status, type } = data;
    const nextStepIndex = index + (action === ACTIONS.PREV ? -1 : 1);

    //kjører neste step når man klikker på neste
    if (EVENTS.STEP_AFTER === type) {
      setStepIndex(nextStepIndex);
    }

    if (!opprettAvtale) {
      //hvis brukeren ikke er inne på et tiltak med opprett avtale, settes opprett avtale-steps til false i localStorage
      setJoyride(joyride => ({ ...joyride, joyrideDetaljerHarVistOpprettAvtale: false }));

      //hopper over steget med opprett avtale for at den skal kjøre videre til neste steg
      if (isStep(data.step, 'opprett-avtale')) {
        setStepIndex(nextStepIndex);
      }
    }

    //resetter joyride ved error
    if (STATUS.ERROR === status) {
      setJoyride(joyride => ({ ...joyride, joyrideDetaljer: true }));
      setStepIndex(0);
    }

    //resetter joyride når den er ferdig eller man klikker skip
    else if (([STATUS.FINISHED, STATUS.SKIPPED] as string[]).includes(status)) {
      logEvent('mulighetsrommet.joyride', { value: 'detaljer', status });
      setJoyride(joyride => ({ ...joyride, joyrideDetaljer: false }));
      setStepIndex(0);
    }

    //lukker joyride ved klikk på escape
    if (ACTIONS.CLOSE === action) {
      setJoyride(joyride => ({ ...joyride, joyrideDetaljer: false }));
      setStepIndex(0);
    }
  };

  return (
    <>
      <JoyrideKnapp
        handleClick={() => {
          setJoyride(joyride => ({ ...joyride, joyrideDetaljer: true }));
          logEvent('mulighetsrommet.joyride', { value: 'detaljer' });
        }}
        className={styles.joyride_detaljer}
      />
      <Joyride
        locale={locale}
        continuous
        run={joyride.joyrideDetaljer}
        steps={steps}
        hideCloseButton
        callback={handleJoyrideCallback}
        showSkipButton
        stepIndex={stepIndex}
        disableScrolling
        styles={styling}
        disableCloseOnEsc={false}
        disableOverlayClose={true}
      />
    </>
  );
}
