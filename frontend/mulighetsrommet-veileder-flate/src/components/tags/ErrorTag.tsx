import { Tag } from '@navikt/ds-react';
import { kebabCase } from '../../utils/Utils';
import { ErrorColored } from '@navikt/ds-icons';

interface Props {
  innhold: string;
  title: string;
  dataTestId: string;
}

export const ErrorTag = ({ innhold, title, dataTestId }: Props) => {
  return (
    <Tag variant="error" size="small" data-testid={`${kebabCase(dataTestId)}`} title={title}>
      <ErrorColored />
      <span style={{ marginLeft: '10px' }}>{innhold}</span>
    </Tag>
  );
};
