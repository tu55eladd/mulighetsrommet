import { GrDocumentPerformance, GrLocation, GrUserAdmin } from "react-icons/gr";
import { FaWpforms } from "react-icons/fa";
import { ImOffice } from "react-icons/im";
import { API_VERSION } from "../sanity.config";
import {
  IKKE_I_ADMINFLATE_TILTAK_DEV,
  IKKE_I_ADMINFLATE_TILTAK_PROD,
  TILTAK_I_EGEN_REGI_PROD,
} from "../utils/utils";

const ORDER_BY_CREATEDAT_FIELD = [{ field: "_createdAt", direction: "desc" }];

const notInAdminFlateFilter = `
  tiltakstype._ref in [
  ${IKKE_I_ADMINFLATE_TILTAK_PROD.concat(IKKE_I_ADMINFLATE_TILTAK_DEV)
    .concat(TILTAK_I_EGEN_REGI_PROD)
    .map((d) => `"${d}"`)
    .join(",")}
]
  `;

export function commonStructure(S, Context) {
  return [
    S.listItem()
      .title("Tiltaksgjennomføringer")
      .icon(GrDocumentPerformance)
      .child(
        S.list()
          .title("Filter")
          .items([
            S.listItem()
              .title("Alle tiltaksgjennomføringer")
              .icon(GrDocumentPerformance)
              .child(
                S.documentList()
                  .title("Alle tiltaksgjennomføringer")
                  .apiVersion(API_VERSION)
                  .filter(`_type == "tiltaksgjennomforing" && ${notInAdminFlateFilter}`)
                  .defaultOrdering([{ field: "_createdAt", direction: "desc" }]),
              ),
            S.divider(),
            S.listItem()
              .title("Per enhet")
              .icon(ImOffice)
              .child(
                S.documentTypeList("enhet")
                  .title("Per enhet")
                  .filter('type == "Lokal"')
                  .apiVersion(API_VERSION)
                  .defaultOrdering([
                    {
                      field: "navn",
                      direction: "asc",
                      ...ORDER_BY_CREATEDAT_FIELD,
                    },
                  ])
                  .child((enhet) =>
                    S.documentList()
                      .apiVersion(API_VERSION)
                      .defaultOrdering(ORDER_BY_CREATEDAT_FIELD)
                      .title("Tiltaksgjennomføringer")
                      .filter(`_type == "tiltaksgjennomforing" && $enhet in enheter[]._ref && ${notInAdminFlateFilter}`)
                      .params({ enhet })
                      .menuItems([...S.documentTypeList("tiltaksgjennomforing").getMenuItems()]),
                  ),
              ),
            S.listItem()
              .title("Per fylke")
              .icon(GrLocation)
              .child(
                S.documentTypeList("enhet")
                  .apiVersion(API_VERSION)
                  .title("Per fylke")
                  .filter('type == "Fylke"')
                  .defaultOrdering([
                    {
                      field: "navn",
                      direction: "asc",
                    },
                  ])
                  .child((enhet) =>
                    S.documentTypeList("tiltakstype")
                      .title("Per tiltakstype")
                      .child((tiltakstype) =>
                        S.documentList()
                          .apiVersion(API_VERSION)
                          .defaultOrdering(ORDER_BY_CREATEDAT_FIELD)
                          .title("Tiltaksgjennomføringer")
                          .filter(
                            `_type == "tiltaksgjennomforing" && ($enhet == fylke._ref) && $tiltakstype == tiltakstype._ref && ${notInAdminFlateFilter}`,
                          )
                          .params({ enhet, tiltakstype })
                          .menuItems([
                            ...S.documentTypeList("tiltaksgjennomforing").getMenuItems(),
                          ]),
                      ),
                  ),
              ),

            S.listItem()
              .title("Per administrator")
              .icon(GrUserAdmin)
              .child(
                S.documentTypeList("redaktor")
                  .title("Per administrator")
                  .child((redaktorId) =>
                    S.documentList()
                      .apiVersion(API_VERSION)
                      .title("Per administrator")
                      .filter(
                        `_type == "tiltaksgjennomforing" && $redaktorId in redaktor[]._ref && ${notInAdminFlateFilter}`,
                      )
                      .params({ redaktorId })
                      .defaultOrdering(ORDER_BY_CREATEDAT_FIELD)
                      .menuItems([...S.documentTypeList("tiltaksgjennomforing").getMenuItems()]),
                  ),
              ),
            S.listItem()
              .title("Per tiltakstype")
              .icon(FaWpforms)
              .child(
                S.documentTypeList("tiltakstype")
                  .title("Per tiltakstype")
                  .defaultOrdering([
                    {
                      field: "tiltakstypeNavn",
                      direction: "asc",
                      ...ORDER_BY_CREATEDAT_FIELD,
                    },
                  ])
                  .child((tiltakstype) =>
                    S.documentList()
                      .apiVersion(API_VERSION)
                      .defaultOrdering(ORDER_BY_CREATEDAT_FIELD)
                      .title("Tiltaksgjennomføringer")
                      .filter(
                        `_type == "tiltaksgjennomforing" && $tiltakstype == tiltakstype._ref && ${notInAdminFlateFilter}`,
                      )
                      .params({ tiltakstype }),
                  ),
              ),
          ]),
      ),
  ];
}
