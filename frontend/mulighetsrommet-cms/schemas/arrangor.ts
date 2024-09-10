import { defineField } from "sanity";

export const arrangor = {
  title: "Arrangør",
  name: "arrangor",
  type: "object",
  fields: [
    defineField({
      title: "Navn på tiltaksarrangør",
      name: "navn",
      type: "string",
    }),
    defineField({
      title: "Kontaktpersoner",
      name: "kontaktpersoner",
      type: "array",
      of: [
        {
          type: "reference",
          to: [{ type: "arrangorKontaktperson" }],
        },
      ],
    }),
  ],
  preview: {
    select: {
      title: "navn",
    },
  },
};
