import i18n from "@/i18n";

class DateUtil {
  public convertDate(dateTime: string): string {
    const date = dateTime.slice(0, dateTime.indexOf("T"));
    const dateParts = date.split("-").map(Number);
    const [year, month, day] = dateParts;
    const options = {
      year: "numeric",
      month: "long",
      day: "numeric",
    } as const;
    return new Intl.DateTimeFormat(i18n.locale, options).format(new Date(year, month - 1, day));
  }
}

export default new DateUtil();
