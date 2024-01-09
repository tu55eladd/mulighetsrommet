import { Tag } from "@navikt/ds-react";
import { kebabCase } from "../../utils/Utils";
import Ikonknapp from "../knapper/Ikonknapp";
import styles from "./Filtertag.module.scss";
import { XMarkIcon } from "@navikt/aksel-icons";

interface FilterTagsProps {
  options: { id: string; tittel: string }[];
  handleClick?: (id: string) => void;
}

const FilterTag = ({ options, handleClick }: FilterTagsProps) => {
  return (
    <>
      {options.map((filtertype) => {
        return (
          <Tag
            size="small"
            variant="info"
            key={filtertype.id}
            data-testid={`filtertag_${kebabCase(filtertype.tittel)}`}
          >
            {filtertype.tittel}
            {handleClick ? (
              <Ikonknapp
                className={styles.overstyrt_ikon_knapp}
                handleClick={() => handleClick(filtertype.id)}
                ariaLabel="Lukke"
                data-testid={`filtertag_lukkeknapp_${kebabCase(filtertype.tittel)}`}
                icon={
                  <XMarkIcon
                    data-testid={`filtertag_lukkeknapp_${kebabCase(filtertype.tittel)}`}
                    className={styles.ikon}
                    aria-label="Lukke"
                  />
                }
              />
            ) : null}
          </Tag>
        );
      })}
    </>
  );
};

export default FilterTag;
