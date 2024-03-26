import { useMutation, useQueryClient } from "@tanstack/react-query";
import { mulighetsrommetClient } from "../../clients";
import { AvtaleNotatRequest } from "mulighetsrommet-api-client";
import { QueryKeys } from "../../QueryKeys";

export function usePutAvtalenotat() {
  const client = useQueryClient();
  return useMutation({
    mutationFn: (notat: AvtaleNotatRequest) => {
      return mulighetsrommetClient.avtaleNotater.lagreAvtalenotat({
        requestBody: notat,
      });
    },
    async onSuccess(_, request) {
      await client.invalidateQueries({
        queryKey: QueryKeys.avtalenotater(request.id),
      });
    },
  });
}
