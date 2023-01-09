import React from "react";
import {
  Checkbox,
  Select,
  TextField,
  UNSAFE_DatePicker,
  UNSAFE_useRangeDatepicker,
} from "@navikt/ds-react";
import { FieldHookConfig, useField } from "formik";
import { SchemaValues } from "./OpprettTiltakstypeSchemaValidation";
import { formaterDato } from "../../../utils/Utils";

export function Tekstfelt({
  label,
  name,
  hjelpetekst,
  ...props
}: {
  name: keyof SchemaValues;
  label: string;
  hjelpetekst?: string;
} & FieldHookConfig<any>) {
  const [field, meta] = useField({ name, ...props });
  return (
    <TextField
      description={hjelpetekst}
      size="small"
      label={label}
      {...field}
      error={meta.touched && meta.error}
    />
  );
}

export function SelectFelt({
  label,
  name,
  defaultBlank = true,
  defaultBlankName = "",
  ...props
}: {
  name: keyof SchemaValues;
  label: string;
  defaultBlank?: boolean;
  defaultBlankName?: string;
} & FieldHookConfig<any>) {
  const [field, meta] = useField({ name, ...props });
  return (
    <Select
      size="small"
      label={label}
      {...field}
      error={meta.touched && meta.error}
    >
      {defaultBlank ? <option value="">{defaultBlankName}</option> : null}
      {props.children}
    </Select>
  );
}

export function CheckboxFelt(
  props: { name: keyof SchemaValues } & FieldHookConfig<any>
) {
  const [field] = useField({ ...props, type: "checkbox" });

  return <Checkbox {...field}>{props.children}</Checkbox>;
}

// TODO Se på uthenting av dato på korrekt format
export function Datovelger() {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [fraDatoField, fraDatoMeta, fraDatoHelper] = useField("fraDato");
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [tilDatoField, tilDatoMeta, tilDatoHelper] = useField("tilDato");
  const { datepickerProps, toInputProps, fromInputProps } =
    UNSAFE_useRangeDatepicker({
      onRangeChange: (val) => {
        // TODO Se på korrekt parsing av dato
        fraDatoHelper.setValue(formaterDato(val?.from));
        tilDatoHelper.setValue(formaterDato(val?.to));
      },
    });

  return (
    <UNSAFE_DatePicker {...datepickerProps}>
      <div style={{ display: "flex", gap: "5rem" }}>
        <DatoFelt
          name="fraDato"
          label="Fra dato"
          {...fromInputProps}
          ref={null}
        />
        <DatoFelt
          name="tilDato"
          label="Til dato"
          {...toInputProps}
          ref={null}
        />
      </div>
    </UNSAFE_DatePicker>
  );
}

export function DatoFelt({
  name,
  label,
  ...rest
}: { name: keyof SchemaValues; label: string } & FieldHookConfig<any> & any) {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [_, meta] = useField({ name, ...rest });
  return (
    <UNSAFE_DatePicker.Input
      {...rest}
      label={label}
      name={name}
      error={meta.touched && meta.error}
    />
  );
}
