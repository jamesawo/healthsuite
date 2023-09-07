import { PermissionsEnum } from '@app/shared/_models/menu/permissions.enum';

export interface MenuBag {
    uuid: string;
    title?: string;
    linkTitle?: string;
    icon?: string;
    hierarchy?: number;
    isSelected?: boolean;
    permissions: MenuItem[];
    isShowChildren?: boolean;
}

export class MenuItem {
    uuid?: number | string;
    title: string;
    linkTitle?: string;
    url?: string;
    right?: PermissionsEnum;
    icon?: string;
    expanded?: boolean;
    children?: MenuItem[];
    hidden?: boolean;
    home?: boolean;
    parent?: MenuItem;
    selected?: boolean;
    identifier?: string;
    isParent?: boolean;
    hasChild?: boolean;
    description?: string;
    displayTitle?: string;
    show?: boolean;
    hierarchy?: number;
}
