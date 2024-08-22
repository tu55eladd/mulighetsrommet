do
$$
    begin
        if exists(select 1 from pg_roles where rolname = 'datastream')
        then
            grant select on arrangor to "datastream";
            alter publication "ds_publication" add table arrangor;

            grant select on avtale to "datastream";
            alter publication "ds_publication" add table avtale;

            grant select on avtale_arrangor_underenhet to "datastream";
            alter publication "ds_publication" add table avtale_arrangor_underenhet;

            grant select on avtale_nav_enhet to "datastream";
            alter publication "ds_publication" add table avtale_nav_enhet;

            grant select on avtale_opsjon_logg to "datastream";
            alter publication "ds_publication" add table avtale_opsjon_logg;

            grant select on avtale_personopplysning to "datastream";
            alter publication "ds_publication" add table avtale_personopplysning;

            grant select on tiltaksgjennomforing_nav_enhet to "datastream";
            alter publication "ds_publication" add table avtale_personopplysning;
        end if;
    end
$$;
