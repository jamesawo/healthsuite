export class NationalityPayload {
    id?: number;
    name?: string;
    childrenString?: string[];
    children?: NationalityPayload[];
    parent?: string;
    hasChildren?: boolean;
    childrenDto?: NationalityPayload[];
}
