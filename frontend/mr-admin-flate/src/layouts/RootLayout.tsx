import { ReactNode } from "react";
import { App } from "../App";
import styles from "./RootLayout.module.scss";

export function RootLayout({ children }: { children: ReactNode }) {
  return (
    <div>
      <main className={styles.container}>{children}</main>
    </div>
  );
}
