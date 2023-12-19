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

// Bump version number when localStorage should be cleared
const version = localStorage.getItem("version");
if (version !== "0.1.2") {
  localStorage.clear();
  localStorage.setItem("version", "0.1.2");
}

/**
 * atomWithStorage fra jotai rendrer først alltid initial value selv om den
 * finnes i storage (https://github.com/pmndrs/jotai/discussions/1879#discussioncomment-5626120)
 * Dette er anbefalt måte og ha en sync versjon av atomWithStorage
 */
function atomWithStorage<Value>(key: string, initialValue: Value, storage = localStorage) {
  const baseAtom = atom(storage.getItem(key) ?? JSON.stringify(initialValue));
  return atom(
    (get) => JSON.parse(get(baseAtom)) as Value,
    (_, set, nextValue: Value) => {
      const str = JSON.stringify(nextValue);
      set(baseAtom, str);
      storage.setItem(key, str);
    },
  );
}

function atomWithHashAndStorage<Value>(key: string, initialValue: Value) {
  const setHash = (hash: string) => {
    const searchParams = new URLSearchParams(window.location.hash.slice(1));
    searchParams.set(key, hash);
    window.history.replaceState(
      null,
      "",
      `${window.location.pathname}${window.location.search}#${searchParams.toString()}`,
    );
  };
  const innerAtom = atomWithStorage(key, initialValue);

  return atom(
    (get) => {
      const value = get(innerAtom);
      setHash(JSON.stringify(value));
      return value;
    },
    (_get, set, newValue: Value) => {
      set(innerAtom, newValue);
      setHash(JSON.stringify(newValue));
    },
  );
}

export const gjennomforingPaginationAtom = atomWithHashAndStorage("page", 1);

export const avtalePaginationAtom = atomWithHashAndStorage("avtalePage", 1);

export interface TiltakstypeFilter {
  sok?: string;
  status?: Tiltakstypestatus;
  kategori?: Tiltakstypekategori;
  sortering?: SorteringTiltakstyper;
}

export const defaultTiltakstypeFilter: TiltakstypeFilter = {
  sok: "",
  status: Tiltakstypestatus.AKTIV,
  kategori: Tiltakstypekategori.GRUPPE,
  sortering: SorteringTiltakstyper.NAVN_ASCENDING,
};

export const tiltakstypeFilterAtom = atomWithHashAndStorage<TiltakstypeFilter>(
  "tiltakstypefilter",
  defaultTiltakstypeFilter,
);

export interface TiltaksgjennomforingFilter {
  search: string;
  navEnheter: string[];
  tiltakstyper: string[];
  statuser: TiltaksgjennomforingStatus[];
  sortering: SorteringTiltaksgjennomforinger;
  navRegioner: string[];
  avtale: string;
  arrangorOrgnr: string[];
  antallGjennomforingerVises: number;
  visMineGjennomforinger: boolean;
}

export const defaultTiltaksgjennomforingfilter: TiltaksgjennomforingFilter = {
  search: "",
  navEnheter: [],
  tiltakstyper: [],
  statuser: [],
  sortering: SorteringTiltaksgjennomforinger.NAVN_ASCENDING,
  navRegioner: [],
  avtale: "",
  arrangorOrgnr: [],
  antallGjennomforingerVises: PAGE_SIZE,
  visMineGjennomforinger: false,
};

export const tiltaksgjennomforingfilterAtom = atomWithHashAndStorage<TiltaksgjennomforingFilter>(
  "tiltaksgjennomforingFilter",
  defaultTiltaksgjennomforingfilter,
);

export const tiltaksgjennomforingfilterForAvtaleAtom =
  atomWithHashAndStorage<TiltaksgjennomforingFilter>(
    "tiltaksgjennomforingFilterForAvtale",
    defaultTiltaksgjennomforingfilter,
  );

export interface AvtaleFilter {
  sok: string;
  statuser: Avtalestatus[];
  navRegioner: string[];
  tiltakstyper: string[];
  sortering: SorteringAvtaler;
  leverandor_orgnr: string[];
  antallAvtalerVises: number;
  visMineAvtaler: boolean;
}

export const defaultAvtaleFilter: AvtaleFilter = {
  sok: "",
  statuser: [],
  navRegioner: [],
  tiltakstyper: [],
  sortering: SorteringAvtaler.NAVN_ASCENDING,
  leverandor_orgnr: [],
  antallAvtalerVises: AVTALE_PAGE_SIZE,
  visMineAvtaler: false,
};

export const avtaleFilterAtom = atomWithHashAndStorage<AvtaleFilter>(
  "avtalefilter",
  defaultAvtaleFilter,
);

export const avtaleFilterForTiltakstypeAtom = atomWithHashAndStorage<AvtaleFilter>(
  "avtalefilterForTiltakstype",
  defaultAvtaleFilter,
);

export const gjennomforingDetaljerTabAtom = atom<string>("detaljer");

export const avtaleDetaljerTabAtom = atom<"detaljer" | "redaksjonelt-innhold">("detaljer");
