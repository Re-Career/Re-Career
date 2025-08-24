// 초기 스크롤 위치 계산
export const calculateScrollPosition = <T>({
  items,
  itemHeight,
  selectedItem,
}: {
  items: T[]
  itemHeight: number
  selectedItem: T
}): number => {
  return items.indexOf(selectedItem) * itemHeight
}
