import { atomWithHash } from "jotai-location";
import {
  Avtalestatus,
  SorteringAvtaler,
  SorteringTiltaksgjennomforinger,
  SorteringTiltakstyper,
  TiltaksgjennomforingStatus,
  Tiltakstypekategori,
  Tiltakstypestatus,
} from "mulighetsrommet-api-client";
import { atom } from "jotai";
import { AVTALE_PAGE_SIZE, PAGE_SIZE } from "../constants";

export const paginationAtom = atomWithHash("page", 1, {
  setHash: "replaceState",
});

export const avtalePaginationAtom = atomWithHash("avtalePage", 1, {
  setHash: "replaceState",
});

export const faneAtom = atomWithHash("fane", "tab_notifikasjoner_1", {
  setHash: "replaceState",
});

export interface TiltakstypeFilter {
  sok?: string;
  status: Tiltakstypestatus | "";
  kategori?: Tiltakstypekategori | "";
  sortering?: SorteringTiltakstyper;
}

export const defaultTiltakstypeFilter: TiltakstypeFilter = {
  sok: "",
  status: Tiltakstypestatus.AKTIV,
  kategori: Tiltakstypekategori.GRUPPE,
  sortering: SorteringTiltakstyper.NAVN_ASCENDING,
};

export const tiltakstypeFilter = atomWithHash<TiltakstypeFilter>(
  "tiltakstypefilter",
  defaultTiltakstypeFilter,
  {
    setHash: "replaceState",
  }
);

export interface Tiltaksgjennomforingfilter {
  search: string;
  enhet: string;
  tiltakstype: string;
  status: TiltaksgjennomforingStatus | "";
  sortering: SorteringTiltaksgjennomforinger;
  navRegion: string;
  avtale: string;
  arrangorOrgnr: string;
  antallGjennomforingerVises: number;
}

export const defaultTiltaksgjennomforingfilter: Tiltaksgjennomforingfilter = {
  search: "",
  enhet: "",
  tiltakstype: "",
  status: TiltaksgjennomforingStatus.GJENNOMFORES,
  sortering: SorteringTiltaksgjennomforinger.NAVN_ASCENDING,
  navRegion: "",
  avtale: "",
  arrangorOrgnr: "",
  antallGjennomforingerVises: PAGE_SIZE,
};

export const tiltaksgjennomforingfilter =
  atomWithHash<Tiltaksgjennomforingfilter>(
    "tiltaksgjennomforingFilter",
    defaultTiltaksgjennomforingfilter,
    {
      setHash: "replaceState",
    }
  );

export const tiltaksgjennomforingTilAvtaleFilter = atom<
  Pick<Tiltaksgjennomforingfilter, "search">
>({ search: "" });

export type AvtaleTabs = "avtaleinfo" | "tiltaksgjennomforinger" | "nokkeltall";

export interface AvtaleFilterProps {
  sok: string;
  status: Avtalestatus | "";
  navRegion: string;
  tiltakstype: string;
  sortering: SorteringAvtaler;
  leverandor_orgnr: string;
  antallAvtalerVises: number;
  avtaleTab: AvtaleTabs;
}

export const defaultAvtaleFilter: AvtaleFilterProps = {
  sok: "",
  status: Avtalestatus.AKTIV,
  navRegion: "",
  tiltakstype: "",
  sortering: SorteringAvtaler.NAVN_ASCENDING,
  leverandor_orgnr: "",
  antallAvtalerVises: AVTALE_PAGE_SIZE,
  avtaleTab: "avtaleinfo",
};

const avtaleFilter = atomWithHash<AvtaleFilterProps>(
  "avtalefilter",
  defaultAvtaleFilter,
  { setHash: "replaceState" }
);

export type TiltakstypeAvtaleTabs = "arenaInfo" | "avtaler";

export const avtaleTabAtom = atomWithHash<TiltakstypeAvtaleTabs>(
  "avtaleTab",
  "arenaInfo",
  {
    setHash: "replaceState",
  }
);

export { avtaleFilter };

export type TiltaksgjennomforingerTabs = "detaljer" | "nokkeltall";

export const tiltaksgjennomforingTabAtom =
  atomWithHash<TiltaksgjennomforingerTabs>(
    "tiltaksgjennomforingTab",
    "detaljer",
    {
      setHash: "replaceState",
    }
  );
