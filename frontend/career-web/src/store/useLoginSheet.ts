import { create } from 'zustand'

interface LoginSheetState {
  isOpen: boolean
  onOpen: () => void
  onClose: () => void
}

export const useLoginSheet = create<LoginSheetState>((set) => ({
  isOpen: false,
  onOpen: () => set({ isOpen: true }),
  onClose: () => set({ isOpen: false }),
}))
