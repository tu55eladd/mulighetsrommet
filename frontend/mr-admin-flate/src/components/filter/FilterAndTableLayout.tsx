import { Tabs } from "@navikt/ds-react";
import { useState } from "react";
import styles from "./Filter.module.scss";
import { Separator } from "../detaljside/Metadata";
import { FunnelIcon } from "@navikt/aksel-icons";

interface Props {
  filter: React.ReactNode;
  buttons: React.ReactNode;
  tags: React.ReactNode;
  table: React.ReactNode;
}

export function FilterAndTableLayout(props: Props) {
  const { filter, table, buttons, tags } = props;
  const [filterSelected, setFilterSelected] = useState<boolean>(true);

  return (
    <div className={styles.container}>
      <Tabs className={styles.filter_tabs} size="medium" value={filterSelected ? "filter" : ""}>
        <Tabs.List>
          <Tabs.Tab
            className={styles.filter_tab}
            onClick={() => setFilterSelected(!filterSelected)}
            value="filter"
            data-testid="filter-tab"
            label="Filter"
            icon={<FunnelIcon title="filter" />}
            aria-controls="filter"
          />
        </Tabs.List>
      </Tabs>
      <div className={styles.button_row}>{buttons}</div>
      <div
        id="filter"
        style={{ display: filterSelected ? "grid" : "none" }}
        className={styles.filter}
      >
        {filter}
      </div>
      <div className={styles.tags_and_table_container}>
        <Separator style={{ marginBottom: "0.25rem", marginTop: "0" }} />
        {tags}
        <div className={styles.table}>{table}</div>
      </div>
    </div>
  );
}