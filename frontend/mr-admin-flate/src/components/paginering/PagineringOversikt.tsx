import { BodyShort, Select } from "@navikt/ds-react";
import styles from "./PagineringOversikt.module.scss";

interface Props {
  page: number;
  pageSize: number;
  onChangePageSize?: (value: number) => void;
  antall: number;
  maksAntall: number;
  type: string;
}

const antallSize = [15, 50, 100, 250, 500, 1000];

export function PagineringsOversikt({
  page,
  pageSize,
  onChangePageSize,
  antall,
  maksAntall,
  type,
}: Props) {
  const start = (page - 1) * pageSize + 1;
  const end = antall + (page - 1) * pageSize;
  const summary =
    antall === 0 ? `Viser 0 av 0 ${type}` : `Viser ${start}-${end} av ${maksAntall} ${type}`;
  return (
    <div className={styles.container}>
      <BodyShort weight="semibold">{summary}</BodyShort>

      {onChangePageSize ? (
        <Select
          size="small"
          label="Velg antall"
          hideLabel
          name="size"
          value={pageSize}
          onChange={(e) => onChangePageSize(Number.parseInt(e.currentTarget.value))}
        >
          {antallSize.map((ant) => (
            <option key={ant} value={ant}>
              {ant}
            </option>
          ))}
        </Select>
      ) : null}
    </div>
  );
}
