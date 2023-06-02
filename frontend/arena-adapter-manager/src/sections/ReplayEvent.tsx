import { Section } from "../components/Section";
import { Button, Input, Select } from "@chakra-ui/react";
import { replayEvent } from "../core/api";
import { useState } from "react";
import { useArenaTables } from "../core/hooks";

function ReplayEvent() {
  const { arenaTables, isArenaTablesLoading } = useArenaTables();
  const [arenaId, setArenaId] = useState<string>("");
  const [table, setTable] = useState<string>("");
  const [loading, setLoading] = useState(false);

  const handleReplay = async (table: string, arenaId: string) => {
    setLoading(true);
    await replayEvent(table, arenaId);
    setLoading(false);
  };

  return (
    <Section
      headerText="Replay Event"
      loadingText={"Laster"}
      isLoading={isArenaTablesLoading}
    >
      <Select
        placeholder="Velg tabell"
        value={table}
        onChange={({ currentTarget }) => {
          setTable(currentTarget.value);
        }}
      >
        {arenaTables.map((table) => (
          <option key={table} value={table}>
            {table}
          </option>
        ))}
      </Select>
      <Input
        placeholder="Arena id"
        value={arenaId}
        onChange={({ currentTarget }) => {
          setArenaId(currentTarget.value);
        }}
      />
      <Button disabled={loading} onClick={() => handleReplay(table, arenaId)}>
        {loading ? "Replaying event" : "Replay Event"}
      </Button>
    </Section>
  );
}

export default ReplayEvent;
