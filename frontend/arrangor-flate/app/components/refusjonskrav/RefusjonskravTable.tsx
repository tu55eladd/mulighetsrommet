import { Refusjonskrav, RefusjonskravStatus } from "@mr/api-client";
import { Table, Tag } from "@navikt/ds-react";
import { Link } from "@remix-run/react";
import { ReactNode } from "react";

interface Props {
  krav: Refusjonskrav[];
}

export function RefusjonskravTable({ krav }: Props) {
  return (
    <>
      <Table>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>Tiltaksnr.</Table.HeaderCell>
            <Table.HeaderCell>Kravnr.</Table.HeaderCell>
            <Table.HeaderCell>Periode</Table.HeaderCell>
            <Table.HeaderCell>Beløp</Table.HeaderCell>
            <Table.HeaderCell>Frist for godkjenning</Table.HeaderCell>
            <Table.HeaderCell>Status</Table.HeaderCell>
            <Table.HeaderCell></Table.HeaderCell>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {krav.map(({ id, tiltaksnr, kravnr, periode, belop, fristForGodkjenning, status }) => {
            return (
              <Table.Row
                className={
                  status === RefusjonskravStatus.NARMER_SEG_FRIST
                    ? "bg-surface-warning-moderate"
                    : ""
                }
                key={id}
              >
                <Table.DataCell>{tiltaksnr}</Table.DataCell>
                <Table.DataCell>{kravnr}</Table.DataCell>
                <Table.DataCell>{periode}</Table.DataCell>
                <Table.DataCell>{belop} NOK</Table.DataCell>
                <Table.DataCell>{fristForGodkjenning}</Table.DataCell>
                <Table.DataCell>{statusTilTag(status)}</Table.DataCell>
                <Table.DataCell>
                  <Link className="hover:underline" to={`for-du-begynner/${id}`}>
                    Gå til krav
                  </Link>
                </Table.DataCell>
              </Table.Row>
            );
          })}
        </Table.Body>
      </Table>
    </>
  );
}

function statusTilTag(status: RefusjonskravStatus): ReactNode {
  switch (status) {
    case RefusjonskravStatus.ATTESTERT:
      return <Tag variant="neutral">Attestert</Tag>;
    case RefusjonskravStatus.KLAR_FOR_INNSENDING:
      return <Tag variant="alt1">Klar for innsending</Tag>;
    case RefusjonskravStatus.NARMER_SEG_FRIST:
      return <Tag variant="warning">Nærmer seg frist</Tag>;
  }
}
