export interface OptionView {
  description: string;
  gotoId: number;
}

export interface SectionView {
  text: string;
  options: OptionView[];
  isEnding: boolean;
  health: number;
  isDead: boolean;
}

export interface GameStartResult {
  sessionId: string;
  section: SectionView;
}

