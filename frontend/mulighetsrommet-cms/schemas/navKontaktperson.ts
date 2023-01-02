import { GrUserWorker } from "react-icons/gr";
import { defineField, defineType } from "sanity";
import { Information } from "../components/Information";

export const navKontaktperson = defineType({
  name: "navKontaktperson",
  title: "NAV kontaktperson",
  type: "document",
  icon: GrUserWorker,
  fields: [
    defineField({
      name: "information",
      title: " ",
      type: "string",
      components: {
        input: Information,
      },
    }),
    defineField({
      name: "navn",
      title: "Navn",
      type: "string",
      validation: (rule) => rule.required().min(2).max(200),
    }),
    defineField({
      name: "enhet",
      title: "NAV-enhet",
      type: "string",
      validation: (rule) => rule.required().min(2).max(200),
    }),
    defineField({
      name: "telefonnummer",
      title: "Telefonnummer",
      type: "string",
      validation: (rule) => rule.required().min(2).max(200),
    }),
    defineField({
      name: "epost",
      title: "E-post",
      type: "string",
      validation: (rule) => rule.required().min(2).max(200),
    }),
  ],
  preview: {
    select: {
      title: "navn",
    },
  },
});
