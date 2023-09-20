import { useQuery } from "@tanstack/react-query";
import { NavEnhetStatus } from "mulighetsrommet-api-client";
import { mulighetsrommetClient } from "../clients";
import { QueryKeys } from "../QueryKeys";

export function useNavEnheter(
  statuser: NavEnhetStatus[] = [
    NavEnhetStatus.AKTIV,
    NavEnhetStatus.UNDER_AVVIKLING,
    NavEnhetStatus.UNDER_ETABLERING,
  ],
) {
  return useQuery(QueryKeys.enheter(), () => {
    return mulighetsrommetClient.navEnheter.getEnheter({
      statuser,
    });
  });
}