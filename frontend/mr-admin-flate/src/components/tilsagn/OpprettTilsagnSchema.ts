import { TilsagnBeregning } from "@mr/api-client";
import z, { ZodIssueCode } from "zod";

const tekster = {
  manglerStartdato: "Du må velge en startdato",
  manglerSluttdato: "Du må velge en sluttdato",
  manglerKostnadssted: "Du må velge et kostnadssted",
  manglerBelop: "Du må skrive inn et beløp for tilsagnet",
} as const;

export const OpprettTilsagnSchema = z
  .object({
    id: z.string().optional().nullable(),
    periode: z.object({
      start: z
        .string({ required_error: tekster.manglerStartdato })
        .min(10, tekster.manglerStartdato),
      slutt: z
        .string({ required_error: tekster.manglerSluttdato })
        .min(10, tekster.manglerSluttdato),
    }),
    kostnadssted: z.string().length(4, tekster.manglerKostnadssted),
    beregning: z.custom<TilsagnBeregning>(),
  })
  .superRefine((data, ctx) => {
    if (data.periode.slutt < data.periode.start) {
      ctx.addIssue({
        code: ZodIssueCode.custom,
        message: "Sluttdato kan ikke være før startdato",
        path: ["periode.slutt"],
      });
    }
  });

export type InferredOpprettTilsagnSchema = z.infer<typeof OpprettTilsagnSchema>;
