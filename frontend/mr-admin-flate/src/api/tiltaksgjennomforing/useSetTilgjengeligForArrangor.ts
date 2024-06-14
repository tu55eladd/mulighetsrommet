import { useMutation, useQueryClient } from "@tanstack/react-query";
import { mulighetsrommetClient } from "@/api/client";
import { QueryKeys } from "@/api/QueryKeys";
import { ApiError } from "mulighetsrommet-api-client";

export function useSetTilgjengeligForArrangor() {
  const queryClient = useQueryClient();

  return useMutation<unknown, ApiError, { id: string; tilgjengeligForArrangorDato: string }>({
    mutationFn: async (data) => {
      return mulighetsrommetClient.tiltaksgjennomforinger.setTilgjengeligForArrangor({
        id: data.id,
        requestBody: { tilgjengeligForArrangorDato: data.tilgjengeligForArrangorDato },
      });
    },

    onSuccess(_, request) {
      return Promise.all([
        queryClient.invalidateQueries({
          queryKey: QueryKeys.tiltaksgjennomforinger(),
        }),
        queryClient.invalidateQueries({
          queryKey: QueryKeys.tiltaksgjennomforing(request.id),
        }),
      ]);
    },
  });
}