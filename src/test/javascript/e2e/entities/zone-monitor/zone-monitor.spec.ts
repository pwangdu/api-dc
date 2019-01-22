/* tslint:disable no-unused-expression */
import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import ZoneMonitorComponentsPage from './zone-monitor.page-object';
import { ZoneMonitorDeleteDialog } from './zone-monitor.page-object';
import ZoneMonitorUpdatePage from './zone-monitor-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('ZoneMonitor e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let zoneMonitorUpdatePage: ZoneMonitorUpdatePage;
  let zoneMonitorComponentsPage: ZoneMonitorComponentsPage;
  let zoneMonitorDeleteDialog: ZoneMonitorDeleteDialog;

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();

    await waitUntilDisplayed(navBarPage.entityMenu);
  });

  it('should load ZoneMonitors', async () => {
    await navBarPage.getEntityPage('zone-monitor');
    zoneMonitorComponentsPage = new ZoneMonitorComponentsPage();
    expect(await zoneMonitorComponentsPage.getTitle().getText()).to.match(/Zone Monitors/);
  });

  it('should load create ZoneMonitor page', async () => {
    await zoneMonitorComponentsPage.clickOnCreateButton();
    zoneMonitorUpdatePage = new ZoneMonitorUpdatePage();
    expect(await zoneMonitorUpdatePage.getPageTitle().getAttribute('id')).to.match(/platformApp.zoneMonitor.home.createOrEditLabel/);
  });

  it('should create and save ZoneMonitors', async () => {
    const nbButtonsBeforeCreate = await zoneMonitorComponentsPage.countDeleteButtons();

    await zoneMonitorUpdatePage.setZoneMonitorIdInput('zoneMonitorId');
    expect(await zoneMonitorUpdatePage.getZoneMonitorIdInput()).to.match(/zoneMonitorId/);
    await waitUntilDisplayed(zoneMonitorUpdatePage.getSaveButton());
    await zoneMonitorUpdatePage.save();
    await waitUntilHidden(zoneMonitorUpdatePage.getSaveButton());
    expect(await zoneMonitorUpdatePage.getSaveButton().isPresent()).to.be.false;

    await zoneMonitorComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await zoneMonitorComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last ZoneMonitor', async () => {
    await zoneMonitorComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await zoneMonitorComponentsPage.countDeleteButtons();
    await zoneMonitorComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    zoneMonitorDeleteDialog = new ZoneMonitorDeleteDialog();
    expect(await zoneMonitorDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/platformApp.zoneMonitor.delete.question/);
    await zoneMonitorDeleteDialog.clickOnConfirmButton();

    await zoneMonitorComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await zoneMonitorComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
