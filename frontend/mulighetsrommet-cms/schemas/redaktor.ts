import { GrUserAdmin } from "react-icons/gr";
import { defineType, defineField } from "sanity";

export const redaktor = defineType({
  name: "redaktor",
  title: "Redaktør",
  type: "document",
  icon: GrUserAdmin,
  fields: [
    defineField({
      name: "navn",
      title: "Navn",
      type: "string",
      validation: (rule) => rule.required().min(2).max(200),
      initialValue: (params, { currentUser }) => {
        const { name } = currentUser;
        return name;
      },
    }),
    defineField({
      name: "epost",
      title: "NAV-epost",
      type: "slug",
      validation: (rule) => rule.required(),
      initialValue: async (params, { currentUser }) => {
        const { email } = currentUser;
        return { _type: "slug", current: email };
      },
    }),
    defineField({
      name: "enhet",
      title: "NAV-enhet",
      description: "Her velger du hvilken NAV-enhet du tilhører",
      type: "string",
      validation: (rule) => rule.required().min(2).max(200),
    }),
  ],
  preview: {
    select: {
      title: "navn",
      subtitle: "enhet",
    },
  },
});
