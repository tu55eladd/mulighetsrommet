import { GrUserWorker } from "react-icons/gr";

export default {
  name: "navkontaktperson",
  title: "NAV kontaktperson",
  type: "document",
  icon: GrUserWorker,
  fields: [
    {
      name: "title",
      title: "Navn",
      type: "string",
      validation: (Rule) => Rule.required().min(2).max(200),
    },
    {
      name: "ident",
      title: "NAV-ident",
      type: "string",
      validation: (Rule) => Rule.required().min(2).max(200),
    },
    {
      name: "enhet",
      title: "NAV-enhet",
      type: "string",
      validation: (Rule) => Rule.required().min(2).max(200),
    },
    {
      name: "telefonnummer",
      title: "Telefonnummer",
      type: "string",
      validation: (Rule) => Rule.required().min(2).max(200),
    },
    {
      name: "epost",
      title: "E-post",
      type: "string",
      validation: (Rule) => Rule.required().min(2).max(200),
    },
  ],
  preview: {
    select: {
      title: "title",
    },
  },
};
