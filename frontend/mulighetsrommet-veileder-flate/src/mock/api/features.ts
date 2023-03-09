import {
  ENABLE_ARBEIDSFLATE,
  Features,
  VIS_INNSIKTSFANE,
  VIS_TILGJENGELIGHETSSTATUS,
} from '../../core/api/feature-toggles';

export const mockFeatures: Features = {
  [ENABLE_ARBEIDSFLATE]: true,
  [VIS_INNSIKTSFANE]: false,
  [VIS_TILGJENGELIGHETSSTATUS]: false,
};
