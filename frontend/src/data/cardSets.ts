import type CardSet from "@/model/CardSet";

export const allCardSets: CardSet[] = [
  {
    name: "fibonacci",
    values: ["1", "2", "3", "5", "8", "13", "21", "34", "55", "?"],
    activeValues: ["1", "2", "3", "5", "8", "13", "21"],
    position: 1,
  },
  {
    name: "t-shirts",
    values: ["XXS", "XS", "S", "M", "L", "XL", "XXL", "XXXL", "?"],
    activeValues: ["XS", "S", "M", "L", "XL"],
    position: 2,
  },
  {
    name: "hours",
    values: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16", "?"],
    activeValues: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16"],
    position: 3,
  },
  {
    name: "numbers",
    values: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "?"],
    activeValues: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
    position: 4,
  },
  {
    name: "custom",
    values: [],
    activeValues: [],
    position: 5,
  },
];

export const allCardSetsWithJiraMode: CardSet[] = [
  {
    name: "fibonacci",
    values: ["1", "2", "3", "5", "8", "13", "21", "34", "55", "?"],
    activeValues: ["1", "2", "3", "5", "8", "13", "21"],
    position: 1,
  },
  {
    name: "hours",
    values: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16", "?"],
    activeValues: ["1", "2", "3", "4", "5", "6", "8", "10", "12", "16"],
    position: 2,
  },
  {
    name: "numbers",
    values: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "?"],
    activeValues: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
    position: 3,
  },
];
